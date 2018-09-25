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
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class HHdEA<S extends Solution<?>> implements Algorithm<List<S>> {

    protected int maxEvaluations;
    protected Problem<S> problem;
    protected List<CooperativeAlgorithm<S>> algorithms;
    private final int populationSize;
    private final String name;
    private final SelectionFunction<CooperativeAlgorithm> selection;
    private final int[] count;
    private final FitnessImprovementRate fir;

    public HHdEA(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxEvaluations,
            Problem problem, String name, SelectionFunction<CooperativeAlgorithm> selection,
            FitnessImprovementRate fir) {
        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.problem = problem;
        this.name = name;
        this.selection = selection;
        JMetalLogger.logger.log(Level.CONFIG, "Selection Function: {0}", selection.getClass().getSimpleName());
        this.fir = fir;
        JMetalLogger.logger.log(Level.CONFIG, "Fitness Improvement Rate: {0}", fir.getClass().getSimpleName());
        this.count = new int[algorithms.size()];
    }

    @Override
    public void run() {

        int evaluations = 0;
        for (CooperativeAlgorithm alg : algorithms) {
            alg.init(populationSize);
            evaluations += alg.getPopulation().size();
            selection.add(alg);
        }
        selection.init();

        while (evaluations < maxEvaluations) {

            // heuristic selection
            CooperativeAlgorithm<S> alg = selection.getNext();
            count[algorithms.indexOf(alg)]++;

            // apply selected heuristic
            List<S> parents = new ArrayList<>();
            for (S s : alg.getPopulation()) {
                parents.add((S) s.copy());
            }
            alg.doIteration();

            // copy the solutions generatedy by alg
            List<S> offspring = new ArrayList<>();
            for (S s : alg.getOffspring()) {
                offspring.add((S) s.copy());
                // count evaluations used by alg
                evaluations++;
            }

            // extract metrics
            double value = fir.getFitnessImprovementRate(parents, offspring);

            // compute reward
            System.out.println("FIR: " + value);
            selection.creditAssignment(value);

            // move acceptance
            // ALL MOVES
            // cooperation phase
            for (CooperativeAlgorithm<S> neighbor : algorithms) {
                if (neighbor != alg) {
                    List<S> migrants = new ArrayList<>();
                    for (S s : offspring) {
                        migrants.add((S) s.copy());
                    }
                    neighbor.receive(migrants);
                }
            }
        }

        System.out.print("Count:\t");
        for (int a = 0; a < count.length; a++) {
            System.out.print(count[a] + "\t");
        }
        System.out.println();

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
