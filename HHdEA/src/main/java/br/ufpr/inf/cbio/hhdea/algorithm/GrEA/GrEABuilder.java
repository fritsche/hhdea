/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche@gmail.com>
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
package br.ufpr.inf.cbio.hhdea.algorithm.GrEA;

import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 *
 * @author Gian Fritsche <gian.fritsche@gmail.com>
 * @param <S>
 */
public class GrEABuilder<S extends Solution> implements AlgorithmBuilder<GrEA<S>> {

    private final Problem<S> problem;
    private int populationSize;
    private int maxEvaluations;
    private int div;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;

    public GrEABuilder(Problem<S> problem) {
        this.problem = problem;
    }

    public Problem getProblem() {
        return this.problem;
    }

    @Override
    public GrEA<S> build() {
        return new GrEA<>(this);
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public GrEABuilder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public GrEABuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public int getDiv() {
        return div;
    }

    public GrEABuilder setDiv(int div) {
        this.div = div;
        return this;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public GrEABuilder setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
        return this;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public GrEABuilder setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
        return this;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public GrEABuilder setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        this.selectionOperator = selectionOperator;
        return this;
    }

}
