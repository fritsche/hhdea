/*
 * Copyright (C) 2018 Gian Fritsche <gmfritsche at inf.ufpr.br>
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
import java.util.logging.Level;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRateCalculator;
import java.util.Observable;
import org.uma.jmetal.algorithm.Algorithm;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public class HHdEA<S extends Solution<?>> extends Observable implements Algorithm<List<S>> {

    private int maxEvaluations;
    private Problem<S> problem;
    protected final int populationSize;
    protected final String name;
    protected final SelectionFunction<CooperativeAlgorithm> selection;
    protected final FitnessImprovementRateCalculator calculator;
    private int evaluations;
    private List<CooperativeAlgorithm<S>> algorithms;
    protected double fir;
    private CooperativeAlgorithm<S> selected;

    public HHdEA(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxEvaluations,
            Problem problem, String name, SelectionFunction<CooperativeAlgorithm> selection,
            FitnessImprovementRateCalculator fir) {

        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.problem = problem;
        this.name = name;
        this.selection = selection;
        JMetalLogger.logger.log(Level.CONFIG, "Selection Function: {0}", selection.getClass().getSimpleName());
        this.calculator = fir;
        JMetalLogger.logger.log(Level.CONFIG, "Fitness Improvement Rate: {0}", fir.getClass().getSimpleName());
    }

    @Override
    public void run() {

        evaluations = 0;
        for (CooperativeAlgorithm alg : algorithms) {
            alg.init(populationSize);
            evaluations += alg.getPopulation().size();
            selection.add(alg);
        }
        selection.init();

        while (evaluations < maxEvaluations) {

            // heuristic selection
            selected = selection.getNext(evaluations / populationSize);

            // apply selected heuristic
            List<S> parents = new ArrayList<>();
            for (S s : selected.getPopulation()) {
                parents.add((S) s.copy());
            }
            selected.doIteration();

            // copy the solutions generatedy by selected
            List<S> offspring = new ArrayList<>();
            for (S s : selected.getOffspring()) {
                offspring.add((S) s.copy());
                // count evaluations used by selected
                evaluations++;
            }

            // extract metrics
            setFir(calculator.computeFitnessImprovementRate(parents, offspring));

            // compute reward
            selection.creditAssignment(getFir());
//            JMetalLogger.logger.log(Level.INFO, "{0}({1})", new Object[]{selected, getFir()});

            // move acceptance
            // ALL MOVES
            // cooperation phase
            for (CooperativeAlgorithm<S> neighbor : algorithms) {
                if (neighbor != selected) {
                    List<S> migrants = new ArrayList<>();
                    for (S s : offspring) {
                        migrants.add((S) s.copy());
                    }
                    neighbor.receive(migrants);
                }
            }
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

    public double getFir() {
        return fir;
    }

    public void setFir(double fir) {
        this.fir = fir;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    public void setProblem(Problem<S> problem) {
        this.problem = problem;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    public List<CooperativeAlgorithm<S>> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<CooperativeAlgorithm<S>> algorithms) {
        this.algorithms = algorithms;
    }

    public CooperativeAlgorithm<S> getSelected() {
        return selected;
    }

    public void setSelected(CooperativeAlgorithm<S> selected) {
        this.selected = selected;
    }
}
