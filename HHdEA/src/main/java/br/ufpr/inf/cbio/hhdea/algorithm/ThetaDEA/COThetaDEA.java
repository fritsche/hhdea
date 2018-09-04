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
package br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA;

import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class COThetaDEA<S extends Solution<?>> extends ThetaDEA implements CooperativeAlgorithm<S> {

    public COThetaDEA(ThetaDEABuilder builder) {
        super(builder);
    }

    private void environmentalSelection() {
        union_ = new ArrayList<>();
        union_.addAll(population_);
        union_.addAll(offspringPopulation_);
        List<S>[] sets = getParetoFronts();
        List<S> firstFront = sets[0];   // the first non-dominated front
        List<S> stPopulation = sets[1]; // the population used in theta-non-dominated ranking
        updateIdealPoint(firstFront);  // update the ideal point
        if (normalize_) {
            updateNadirPoint(firstFront);  // update the nadir point
            normalizePopulation(stPopulation);  // normalize the population using ideal point and nadir point
        }
        getNextPopulation(stPopulation);  // select the next population using theta-non-dominated ranking
    }

    @Override
    public void init(int populationSize) {
        this.populationSize_ = populationSize;
        initializeUniformWeight();
        initPopulation();   // initialize the population;
        initIdealPoint();  // initialize the ideal point
        initNadirPoint();    // initialize the nadir point
        initExtremePoints(); // initialize the extreme points
    }

    @Override
    public void doIteration() {
        createOffSpringPopulation();  // create the offspring population
        environmentalSelection();
    }

    @Override
    public List<S> getPopulation() {
        return population_;
    }

    @Override
    public void receive(List<S> solutions) {
        offspringPopulation_ = solutions;
        environmentalSelection();
    }

    @Override
    public List<S> getOffspring() {
        return offspringPopulation_;
    }

    @Override
    public void overridePopulation(List<S> external) {
        population_ = external;
    }

}
