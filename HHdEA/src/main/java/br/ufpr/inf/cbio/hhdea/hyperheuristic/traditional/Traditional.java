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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.traditional;

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
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * An online learning hyper-heuristic selection based on low-level heursitics.
 * It follows the hyper-heuristic approach and the problem domain barrier from
 * the literature: (Burke, 2013) Burke EK, Gendreau M, Hyde M, Kendall G, Ochoa
 * G, Özcan E, Qu R (2013) Hyper-heuristics: A survey of the state of the art.
 * Journal of the Operational Research Society. (Cowling 2001) Cowling P,
 * Kendall G, Soubeiga E (2001) A hyper-heuristic approach to scheduling a sales
 * summit. In: Selected papers from the Third International Conference on
 * Practice and Theory of Automated Timetabling.
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class Traditional<S extends Solution<?>> implements Algorithm<List<S>> {

    protected List<CooperativeAlgorithm<S>> algorithms;
    private final int populationSize;
    private final int maxEvaluations;
    private final Problem problem;
    private final SelectionFunction<CooperativeAlgorithm> selection;
    private final int[] count;
    private final FitnessImprovementRate fir;
    private List<S> population;
    protected final SolutionListEvaluator<S> evaluator = new SequentialSolutionListEvaluator<>();

    public Traditional(List<CooperativeAlgorithm<S>> algorithms, int populationSize,
            int maxEvaluations, Problem problem,
            SelectionFunction<CooperativeAlgorithm> selection,
            FitnessImprovementRate fir) {
        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.problem = problem;
        this.selection = selection;
        JMetalLogger.logger.log(Level.CONFIG, "Selection Function: {0}", selection.getClass().getSimpleName());
        this.fir = fir;
        JMetalLogger.logger.log(Level.CONFIG, "Fitness Improvement Rate: {0}", fir.getClass().getSimpleName());
        this.count = new int[algorithms.size()];
    }

    @Override
    public void run() {

        for (CooperativeAlgorithm alg : algorithms) {
            // populations generated here will be later overrided
            // however other parameters are also initialized
            alg.init(getPopulationSize());
            selection.add(alg);
        }
        selection.init();

        population = createInitialPopulation();
        population = evaluator.evaluate(population, getProblem());
        int evaluations = population.size();
        while (evaluations < maxEvaluations) {
            // heuristic selection
            CooperativeAlgorithm<S> alg = selection.getNext();
            count[algorithms.indexOf(alg)]++;
            // copy current population
            List<S> populationCopy = new ArrayList<>();
            population.forEach((s) -> {
                populationCopy.add((S) s.copy());
            });
            // send the copy to the selected algorithm
            alg.init(populationCopy);
            // apply heuristic
            alg.doIteration();
            // get new population
            List<S> newPopulation = alg.getPopulation();
            // count FE from alg
            evaluations += newPopulation.size();
            // extract metrics
            double value = fir.getFitnessImprovementRate(population, newPopulation);
            // reward algorithm
            System.out.println("FIR: " + value);
            selection.creditAssignment(value);
            // move acceptance
            // ALL MOVES
            population = newPopulation;
        }

        System.out.print("Count:\t");
        for (int a = 0; a < count.length; a++) {
            System.out.print(count[a] + "\t");
        }
        System.out.println();
    }

    /**
     * Returns the non-dominated solutions from the last applied MOEA.
     *
     * @return
     */
    @Override
    public List<S> getResult() {
        return SolutionListUtils.getNondominatedSolutions(population);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "An online learning hyper-heuristic selection based on low-level heursitics.";
    }

    protected List<S> createInitialPopulation() {
        List<S> population_ = new ArrayList<>(getPopulationSize());
        for (int i = 0; i < getPopulationSize(); i++) {
            S newIndividual = (S) getProblem().createSolution();
            population_.add(newIndividual);
        }
        return population_;
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

    public int[] getCount() {
        return count;
    }

    public FitnessImprovementRate getFir() {
        return fir;
    }

}
