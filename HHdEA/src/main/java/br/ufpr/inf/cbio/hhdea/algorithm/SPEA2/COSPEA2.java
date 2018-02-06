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
package br.ufpr.inf.cbio.hhdea.algorithm.SPEA2;

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.algorithm.multiobjective.spea2.util.EnvironmentalSelection;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class COSPEA2<S extends Solution<?>> extends SPEA2 implements CooperativeAlgorithm<S> {

    private float probability;

    public COSPEA2(Problem problem, int maxIterations, int populationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
        super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
    }

    @Override
    public void setProbability(float probability) {
        this.probability = probability;
    }

    @Override
    public float getProbability() {
        return probability;
    }

    @Override
    public int getPopulationSize(int remainingPopulation, float remainingProbability) {
        return Math.round(remainingPopulation * (probability / remainingProbability));
    }

    @Override
    public List<S> environmentalSelection(List<S> union, int outputSize, double[][] lambda) {
        if (outputSize == 0) {
            return new ArrayList<>(0);
        }
        setMaxPopulationSize(outputSize);
        strenghtRawFitness.computeDensityEstimator(union);
        return (List<S>) new EnvironmentalSelection<>(outputSize).execute((List<Solution<?>>) union);
    }

    @Override
    public List<S> generateOffspring(List<S> population, int N, double[][] lambda) {
        if (N == 0) {
            return new ArrayList<>(0);
        }
        setMaxPopulationSize(N);
        return evaluatePopulation(reproduction(population));
    }

}