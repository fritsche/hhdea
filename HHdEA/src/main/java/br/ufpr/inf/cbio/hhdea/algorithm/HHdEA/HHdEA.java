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

import br.ufpr.inf.cbio.hhdea.metrics.MetricsEvaluator;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class HHdEA<S extends Solution<?>> implements Algorithm<List<S>> {

    protected int maxGenerations;
    protected Problem<S> problem;
    protected List<CooperativeAlgorithm<S>> algorithms;
    private final int populationSize;
    private final String name;
    private MetricsEvaluator metrics;

    public HHdEA(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxGenerations, Problem problem, String name) {
        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.problem = problem;
        this.name = name;
    }

    @Override
    public void run() {

        for (CooperativeAlgorithm alg : algorithms) {
            alg.init(populationSize);
        }
        int generations = algorithms.size();
        int migrationcondition = 1;
        this.metrics = new MetricsEvaluator(problem, populationSize);

        while (generations <= maxGenerations) {

            double rand = JMetalRandom.getInstance().nextDouble();
            int i = 0;
            if (rand < 1.0 / 127.0) {
                i = 0;
            } else if (rand < 3.0 / 127.0) {
                i = 1;
            } else if (rand < 7.0 / 127.0) {
                i = 2;
            } else if (rand < 15.0 / 127.0) {
                i = 3;
            } else if (rand < 31.0 / 127.0) {
                i = 4;
            } else if (rand < 63.0 / 127.0) {
                i = 5;
            } else if (rand < 127.0 / 127.0) {
                i = 6;
            }

            System.out.println(i);

            // for (int i = 0; i < algorithms.size(); i++) {
            algorithms.get(i).doIteration();
            generations++;
            for (int j = 0; j < algorithms.size(); j++) {
                if (i != j) {
                    List<S> migrants = new ArrayList<>();
                    for (S s : algorithms.get(i).getPopulation()) {
                        migrants.add((S) s.copy());
                    }
                    algorithms.get(j).receive(migrants);
                }
            }
            // }
        }
    }

    @Override
    public List<S> getResult() {
        List<S> union = new ArrayList<>();
        for (CooperativeAlgorithm alg : algorithms) {
            union.addAll(alg.getPopulation());
        }
        return SolutionListUtils.getNondominatedSolutions(union);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Hyper-heuristics for distributed Evolutionary Algorithms";
    }

}
