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
package br.ufpr.inf.cbio.hhdea.algorithm.MOEADD;

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.CooperativeAlgorithm;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class COMOEADD<S extends Solution<?>> extends MOEADD<S> implements CooperativeAlgorithm<S> {

    public COMOEADD(MOEADDBuilder builder) {
        super(builder);
    }

    @Override
    public List<S> run(List<S> initialPopulation, int maxEvaluations, double[][] lambda, List<S> extremeSolutions) {
        evaluations_ = 0;

        T_ = 20;
        delta_ = 0.9;

        populationSize_ = initialPopulation.size();
        this.maxEvaluations = maxEvaluations;

        neighborhood_ = new int[populationSize_][T_];
        this.lambda_ = lambda;

        zp_ = new double[problem_.getNumberOfObjectives()]; // ideal point for Pareto-based population
        nzp_ = new double[problem_.getNumberOfObjectives()]; // nadir point for Pareto-based population

        rankIdx_ = new int[populationSize_][populationSize_];
        subregionIdx_ = new int[populationSize_][populationSize_];
        subregionDist_ = new double[populationSize_][populationSize_];

        // STEP 1. Initialization
        initNeighborhood();
        population_ = initialPopulation;
        initIdealPoint();
        initNadirPoint();

        for (S extreme : extremeSolutions) {
            updateReference(extreme, zp_);
            updateNadirPoint(extreme, nzp_);
        }

        // initialize the distance
        for (int i = 0; i < populationSize_; i++) {
            double distance = calculateDistance2(population_.get(i), lambda_[i], zp_, nzp_);
            subregionDist_[i][i] = distance;
        }

        Ranking ranking = (new DominanceRanking()).computeRanking(population_);
        int curRank;
        dominanceRankingAttributeIdentifier = (new DominanceRanking()).getAttributeIdentifier();
        for (int i = 0; i < populationSize_; i++) {
            curRank = (int) population_.get(i).getAttribute(dominanceRankingAttributeIdentifier);
            rankIdx_[curRank][i] = 1;
        }

        // init location
        for (S initial : population_) {
            setLocation(initial, zp_, nzp_);
        }

        // main procedure
        do {
            int[] permutation = new int[populationSize_];
            Utils.randomPermutation(permutation, populationSize_);

            for (int i = 0; i < populationSize_; i++) {
                int cid = permutation[i];

                int type;
                double rnd = randomGenerator.nextDouble();

                // mating selection style
                if (rnd < delta_) {
                    type = 1; // neighborhood
                } else {
                    type = 2; // whole population
                }
                List<S> parents;
                List<S> offSpring;
                parents = matingSelection(cid, type);

                // SBX crossover
                offSpring = crossover_.execute(parents);

                mutation_.execute(offSpring.get(0));
                mutation_.execute(offSpring.get(1));

                problem_.evaluate(offSpring.get(0));
                problem_.evaluate(offSpring.get(1));

                evaluations_ += 2;

                // update ideal points
                updateReference(offSpring.get(0), zp_);
                updateReference(offSpring.get(1), zp_);

                // update nadir points
                updateNadirPoint(offSpring.get(0), nzp_);
                updateNadirPoint(offSpring.get(1), nzp_);

                updateArchive(offSpring.get(0));
                updateArchive(offSpring.get(1));
            } // for			
        } while (evaluations_ < maxEvaluations);

        return population_;

    }

}