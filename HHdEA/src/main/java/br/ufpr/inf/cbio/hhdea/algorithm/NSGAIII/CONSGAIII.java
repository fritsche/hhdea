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
package br.ufpr.inf.cbio.hhdea.algorithm.NSGAIII;

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class CONSGAIII<S extends Solution<?>> extends NSGAIII implements CooperativeAlgorithm<S> {

    private float probability;

    public CONSGAIII(CONSGAIIIBuilder builder) {
        super(builder);
    }

    protected List<S> replacement(List<S> union_) {
        // Ranking the union
        Ranking ranking = (new DominanceRanking()).computeRanking(union_);

        int remain = populationSize_;
        int index = 0;
        List<S> front;
        population_ = new ArrayList<>();

        // Obtain the next front
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {

            for (int k = 0; k < front.size(); k++) {
                population_.add(front.get(k));
            } // for

            // Decrement remain
            remain = remain - front.size();

            // Obtain the next front
            index++;
            if (remain > 0) {
                front = ranking.getSubfront(index);
            } // if
        }

        if (remain > 0) { // front contains individuals to insert

            new Niching(population_, front, lambda_, remain, normalize_)
                    .execute();
        }

        return population_;
    }

    public static int roundEven(float d) {
        return Math.round(d / 2) * 2;
    }

    @Override
    public void setQuota(float probability) {
        this.probability = probability;
    }

    @Override
    public float getQuota() {
        return probability;
    }

    @Override
    public int getPopulationSize(int remainingPopulation, float remainingProbability) {
        return roundEven(remainingPopulation * (probability / remainingProbability));
    }

    @Override
    public List<S> environmentalSelection(List<S> union, int outputSize, double[][] lambda) {
        if (outputSize == 0) {
            return new ArrayList<>(0);
        }
        this.lambda_ = lambda;
        this.populationSize_ = outputSize;
        return replacement(union);
    }

    @Override
    public List<S> generateOffspring(List<S> population, int N, double[][] lambda) {
        if (N == 0) {
            return new ArrayList<>(0);
        }
        this.lambda_ = lambda;
        this.populationSize_ = N;
        offspringPopulation_ = new ArrayList<>(populationSize_);
        for (int i = 0; i < (populationSize_ / 2); i++) {
            // obtain parents

            List<S> parents = new ArrayList<>();
            parents.add((S) selection_.execute(population));
            parents.add((S) selection_.execute(population));

            List<S> offSpring = (List<S>) crossover_.execute(parents);

            mutation_.execute(offSpring.get(0));
            mutation_.execute(offSpring.get(1));

            problem_.evaluate(offSpring.get(0));
            problem_.evaluate(offSpring.get(1));

            offspringPopulation_.add(offSpring.get(0));
            offspringPopulation_.add(offSpring.get(1));

        } // for

        return offspringPopulation_;
    }

}
