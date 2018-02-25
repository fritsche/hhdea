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
            System.out.println(migrationcondition);

            for (CooperativeAlgorithm alg : algorithms) {
                alg.doIteration();
                generations++;
            }

            if (migrationcondition % 10 == 0) {
                for (int i = 0; i < algorithms.size(); i++) {
                    List<S> migrants = new ArrayList<>();
                    for (int j = 0; j < algorithms.size(); j++) {
                        if (i != j) {
                            for (S s : algorithms.get(j).getPopulation()) {
                                migrants.add((S) s.copy());
                            }
                        }
                    }
                    algorithms.get(i).receive(migrants);
                }
            }
            migrationcondition++;
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
