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
package br.ufpr.inf.cbio.hhdea.algorithm.HHdEA;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public interface CooperativeAlgorithm<S extends Solution<?>> {

    public void setProbability(float probability);
    public float getProbability();

    /**
     * Return the population size given a probability. Algorithms that needs
     * even population size must check it here.
     *
     * @param remainingPopulation
     * @param remainingProbability
     * @return subPopulationSize
     */
    public int getPopulationSize(int remainingPopulation, float remainingProbability);

    /**
     * Filter the set of solutions using the MOEA environmental selection
     * strategy.
     *
     * @param union
     * @param outputSize
     * @param lambda
     * @return subset of solutions of size outputSize
     */
    public List<S> environmentalSelection(List<S> union, int outputSize, double[][] lambda);

    /**
     * Generate offspring using the MOEA reproduction strategy. Reproduce and
     * return offspring.
     *
     * @param population
     * @param N
     * @param lambda (optional) Weight Vectors for decompotision based MOEAs
     * @return offspring
     */
    public List<S> generateOffspring(List<S> population, int N, double lambda[][]);

}
