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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.CooperativeAlgorithm;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.selection.SelectionFunction;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRateCalculator;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public class HHdEABuilder<S extends Solution<?>> implements AlgorithmBuilder<HHdEA<S>> {

    protected List<CooperativeAlgorithm> algorithms;
    protected int populationSize;
    protected int maxEvaluations;
    protected final Problem problem;
    protected String name;
    protected SelectionFunction<CooperativeAlgorithm> selection;
    protected FitnessImprovementRateCalculator fir;

    public HHdEABuilder(Problem problem) {
        this.problem = problem;
        name = "HHdEA"; // default name
    }

    public String getName() {
        return name;
    }

    public HHdEABuilder setName(String name) {
        this.name = name;
        return this;
    }

    public List<CooperativeAlgorithm> getAlgorithms() {
        return algorithms;
    }

    public HHdEABuilder setAlgorithms(List<CooperativeAlgorithm> algorithms) {
        this.algorithms = algorithms;
        return this;
    }

    public HHdEABuilder addAlgorithm(CooperativeAlgorithm algorithm) {
        if (algorithms == null) {
            algorithms = new ArrayList<>();
        }
        algorithms.add(algorithm);
        return this;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public HHdEABuilder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public HHdEABuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public SelectionFunction<CooperativeAlgorithm> getSelection() {
        return selection;
    }

    public HHdEABuilder setSelection(SelectionFunction<CooperativeAlgorithm> selection) {
        this.selection = selection;
        return this;
    }

    public FitnessImprovementRateCalculator getFir() {
        return fir;
    }

    public HHdEABuilder setFir(FitnessImprovementRateCalculator fir) {
        this.fir = fir;
        return this;
    }

    @Override
    public HHdEA build() {
        return new HHdEA(algorithms, populationSize, maxEvaluations, problem, name,
                selection, fir);
    }

}
