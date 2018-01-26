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

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class COThetaDEA<S extends Solution<?>> extends ThetaDEA implements CooperativeAlgorithm<S> {

    private float probability;

    public COThetaDEA(COThetaDEABuilder builder) {
        super(builder);
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
        this.lambda_ = lambda;
        this.populationSize_ = outputSize;

        this.population_ = union;

        // initIdealPoint();  // initialize the ideal point
        // initNadirPoint();    // initialize the nadir point
        // initExtremePoints(); // initialize the extreme points
        this.union_ = this.population_;

        List<S>[] sets = getParetoFronts();

        List<S> firstFront = sets[0];   // the first non-dominated front
        List<S> stPopulation = sets[1]; // the population used in theta-non-dominated ranking

        updateIdealPoint(firstFront);  // update the ideal point
        if (normalize_) {
            updateNadirPoint(firstFront);  // update the nadir point
            normalizePopulation(stPopulation);  // normalize the population using ideal point and nadir point
        }

        getNextPopulation(stPopulation);  // select the next population using theta-non-dominated ranking

        return population_;
    }

    @Override
    public List<S> generateOffspring(List<S> population, int N, double[][] lambda) {
        if (N == 0) {
            return new ArrayList<>(0);
        }
        this.lambda_ = lambda;
        this.populationSize_ = N;
        this.population_ = population;
        createOffSpringPopulation();
        return offspringPopulation_;
    }

}
