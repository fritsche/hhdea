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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA3;

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
public class HHdEA3Builder<S extends Solution<?>> implements AlgorithmBuilder<HHdEA3<S>> {

    protected List<CooperativeAlgorithm> algorithms;
    protected int populationSize;
    protected int maxEvaluations;
    protected final Problem problem;
    protected String name;
    protected SelectionFunction<CooperativeAlgorithm> selection;
    protected FitnessImprovementRateCalculator fir;

    public HHdEA3Builder(Problem problem) {
        this.problem = problem;
        name = "HHdEA3"; // default name
    }

    public String getName() {
        return name;
    }

    public HHdEA3Builder setName(String name) {
        this.name = name;
        return this;
    }

    public List<CooperativeAlgorithm> getAlgorithms() {
        return algorithms;
    }

    public HHdEA3Builder setAlgorithms(List<CooperativeAlgorithm> algorithms) {
        this.algorithms = algorithms;
        return this;
    }

    public HHdEA3Builder addAlgorithm(CooperativeAlgorithm algorithm) {
        if (algorithms == null) {
            algorithms = new ArrayList<>();
        }
        algorithms.add(algorithm);
        return this;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public HHdEA3Builder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public HHdEA3Builder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public SelectionFunction<CooperativeAlgorithm> getSelection() {
        return selection;
    }

    public HHdEA3Builder setSelection(SelectionFunction<CooperativeAlgorithm> selection) {
        this.selection = selection;
        return this;
    }

    public FitnessImprovementRateCalculator getFir() {
        return fir;
    }

    public HHdEA3Builder setFir(FitnessImprovementRateCalculator fir) {
        this.fir = fir;
        return this;
    }

    @Override
    public HHdEA3 build() {
        return new HHdEA3(algorithms, populationSize, maxEvaluations, problem, name,
                selection, fir);
    }

}
