/*
 * Copyright (C) 2018 Gian Fritsche <gmfritsche at inf.ufpr.br>
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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.traditional;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.CooperativeAlgorithm;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.selection.SelectionFunction;
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRate;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public class TraditionalBuilder<S extends Solution<?>> implements AlgorithmBuilder<Traditional<S>> {

    private List<CooperativeAlgorithm<S>> algorithms;
    private int populationSize;
    private int maxEvaluations;
    private final Problem problem;
    private SelectionFunction<CooperativeAlgorithm> selection;
    private FitnessImprovementRate fir;

    public TraditionalBuilder(Problem problem) {
        this.problem = problem;
    }

    public List<CooperativeAlgorithm<S>> getAlgorithms() {
        return algorithms;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public Problem getProblem() {
        return problem;
    }

    public SelectionFunction<CooperativeAlgorithm> getSelection() {
        return selection;
    }

    public FitnessImprovementRate getFir() {
        return fir;
    }

    public TraditionalBuilder setAlgorithms(List<CooperativeAlgorithm<S>> algorithms) {
        this.algorithms = algorithms;
        return this;
    }

    public TraditionalBuilder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public TraditionalBuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public TraditionalBuilder setSelection(SelectionFunction<CooperativeAlgorithm> selection) {
        this.selection = selection;
        return this;
    }

    public TraditionalBuilder setFir(FitnessImprovementRate fir) {
        this.fir = fir;
        return this;
    }

    public TraditionalBuilder addAlgorithm(CooperativeAlgorithm algorithm) {
        if (algorithms == null) {
            algorithms = new ArrayList<>();
        }
        algorithms.add(algorithm);
        return this;
    }

    @Override
    public Traditional<S> build() {
        return new Traditional<>(algorithms, populationSize, maxEvaluations, problem,
                selection, fir);
    }

}
