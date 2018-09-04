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
package br.ufpr.inf.cbio.hhdea.algorithm.HypE;

import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class COHypE<S extends Solution<?>> extends HypE implements CooperativeAlgorithm<S> {

    public COHypE(HypEBuilder builder) {
        super(builder);
    }

    @Override
    public void init(int populationSize) {
        population = new ArrayList<>(populationSize);
        reference = (S) problem.createSolution();
        evaluations = 0;
        for (int i = 0; i < populationSize; i++) {
            S newSolution = (S) problem.createSolution();
            problem.evaluate(newSolution);
            for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
                if (newSolution.getObjective(j) > reference.getObjective(j)) {
                    reference.setObjective(j, newSolution.getObjective(j));
                }
            }
            evaluations++;
            population.add(newSolution);
        }
        for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
            reference.setObjective(j, reference.getObjective(j) * 1.2);
        }
    }

    @Override
    public void doIteration() {

        offspringPopulation = new ArrayList<>(populationSize);
        fs.setHypEFitness(population, reference, populationSize, samples);
        for (int i = 0; i < (populationSize / 2); i++) {
            List<S> parents = new ArrayList<>();

            parents.add((S) selectionOperator.execute(population));
            parents.add((S) selectionOperator.execute(population));

            List<S> offSpring = (List<S>) crossoverOperator.execute(parents);

            mutationOperator.execute(offSpring.get(0));
            mutationOperator.execute(offSpring.get(1));

            problem.evaluate(offSpring.get(0));
            problem.evaluate(offSpring.get(1));
            evaluations += 2;
            offspringPopulation.add(offSpring.get(1));
            offspringPopulation.add(offSpring.get(0));
        }

        environmentalSelection();
    }

    @Override
    public List<S> getPopulation() {
        return population;
    }

    @Override
    public List<S> getOffspring() {
        return offspringPopulation;
    }

    @Override
    public void receive(List<S> solutions) {
        offspringPopulation = solutions;
        environmentalSelection();
    }

    @Override
    public void overridePopulation(List<S> external) {
        population.clear();
        population.addAll(external);
    }

}
