/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche at gmail.com>
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
package br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 *
 * @author Gian Fritsche <gian.fritsche at gmail.com>
 * @param <S>
 */
public class SPEA2SDEBuilder<S extends Solution> implements AlgorithmBuilder<SPEA2SDE<S>> {

    private int populationSize;
    private int archiveSize;
    private int maxEvaluations;
    private CrossoverOperator crossover;
    private MutationOperator mutation;
    private SelectionOperator selection;
    private final Problem<S> problem;

    SPEA2SDEBuilder(Problem<S> problem) {
        this.problem = problem;
    }

    @Override
    public SPEA2SDE<S> build() {
        return new SPEA2SDE(this);
    }

    public Problem getProblem() {
        return this.problem;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public SPEA2SDEBuilder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public SPEA2SDEBuilder setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;
        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public SPEA2SDEBuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public CrossoverOperator getCrossover() {
        return crossover;
    }

    public SPEA2SDEBuilder setCrossover(CrossoverOperator crossover) {
        this.crossover = crossover;
        return this;
    }

    public MutationOperator getMutation() {
        return mutation;
    }

    public SPEA2SDEBuilder setMutation(MutationOperator mutation) {
        this.mutation = mutation;
        return this;
    }

    public SelectionOperator getSelection() {
        return selection;
    }

    public SPEA2SDEBuilder setSelection(SelectionOperator selection) {
        this.selection = selection;
        return this;
    }

}
