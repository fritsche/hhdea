/*
 * Copyright (C) 2018 Gian Fritsche <gmfritsche@inf.ufpr.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.ufpr.inf.cbio.hhdea.algorithm.MOEAD;

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class COMOEAD<S extends Solution<?>> extends MOEAD implements CooperativeAlgorithm<S> {

    private List<S> offspring;

    public COMOEAD(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
        offspring = new ArrayList<>(populationSize);
    }

    @Override
    public void init(int populationSize) {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();
    }

    @Override
    public void doIteration() {

        // all solutions generated in this iteration
        offspring.clear();

        int[] permutation = new int[populationSize];
        MOEADUtils.randomPermutation(permutation, populationSize);

        for (int i = 0; i < populationSize; i++) {
            int subProblemId = permutation[i];

            NeighborType neighborType = chooseNeighborType();
            List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

            differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
            List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

            DoubleSolution child = children.get(0);
            mutationOperator.execute(child);
            problem.evaluate(child);
            offspring.add((S) child);

            updateIdealPoint(child);
            updateNeighborhood(child, subProblemId, neighborType);
        }
    }

    @Override
    public List<S> getPopulation() {
        return (List<S>) this.population;
    }

    @Override
    public void receive(List<S> solutions) {
        for (S s : solutions) {
            int subProblemId = JMetalRandom.getInstance().nextInt(0, populationSize - 1);
            NeighborType neighborType = chooseNeighborType();
            updateIdealPoint((DoubleSolution) s);
            updateNeighborhood((DoubleSolution) s, subProblemId, neighborType);
        }

    }

    @Override
    public List<S> getOffspring() {
        return offspring;
    }

    @Override
    public void copyPopulation(List<S> external) {
        population.clear();
        population.addAll((Collection<? extends DoubleSolution>) external);
    }
}
