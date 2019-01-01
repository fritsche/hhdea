/*
 * Copyright (C) 2019 Gian Fritsche <gmfritsche at inf.ufpr.br>
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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.HHLA;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.CooperativeAlgorithm;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.selection.SelectionFunction;
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRateCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;

/**
 *
 * Li, W., Ozcan, E., & John, R. (2017). A Learning Automata based
 * Multiobjective Hyper-heuristic. IEEE Transactions on Evolutionary
 * Computation, (c), 1–15. https://doi.org/10.1109/TEVC.2017.2785346
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class HHLA<S extends Solution<?>> extends Observable implements Algorithm<List<S>> {

    private int maxEvaluations;
    private Problem<S> problem;
    protected final int populationSize;
    protected final String name;
    protected final SelectionFunction<CooperativeAlgorithm> selection;
    protected final FitnessImprovementRateCalculator calculator;
    private int evaluations;
    private List<CooperativeAlgorithm<S>> algorithms;
    private double improvement;
    private CooperativeAlgorithm<S> selected;
    private int k; // maximum number of iterations K for applying a low level MOEA
    private List<S> popcurr;
    private double deltaV; // threshold value for improvement

    public HHLA(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxEvaluations,
            Problem problem, String name, SelectionFunction<CooperativeAlgorithm> selection,
            FitnessImprovementRateCalculator fir, int k, double deltaV) {

        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.problem = problem;
        this.name = name;
        this.selection = selection;
        JMetalLogger.logger.log(Level.CONFIG, "Selection Function: {0}", selection.getClass().getSimpleName());
        this.calculator = fir;
        JMetalLogger.logger.log(Level.CONFIG, "Fitness Improvement Rate: {0}", fir.getClass().getSimpleName());
        this.k = k;
        this.deltaV = deltaV;
    }

    @Override
    public void run() {

        // 1. [A, P, hi, Popcurr] <- Initialise(H) ;
        for (CooperativeAlgorithm alg : algorithms) {
            selection.add(alg);
        }
        selection.init();

        popcurr = new ArrayList<>(populationSize);
        evaluations = 0;
        for (int i = 0; i < populationSize; i++) {
            S newSolution = (S) problem.createSolution();
            problem.evaluate(newSolution);
            evaluations++;
            popcurr.add(newSolution);
        }
        List<S> popnext = null;
        selected = selection.getNext();
        selected.init(popcurr);

        // 2. while (termination criteria not satisfied) do
        while (evaluations < maxEvaluations) {

            // 3. Popnext <- ApplyMetaheuristic(hi, Popcurr, g);    
            int g = Math.min((int) Math.ceil((maxEvaluations - evaluations) / (double) populationSize), k);
            if (g == 0) {
                break;
            }
            for (int i = 0; i < g; i++) {
                selected.doIteration();
                popnext = selected.getPopulation();
                evaluations += popnext.size();
            }

            // 5. if (switch()) then
            if (hasImprovement(popcurr, popnext)) {
                // 6. LearningAutomataUpdateScheme(P);

                /**
                 * Li, W., Ozcan, E., & John, R. (2017) uses the difference of
                 * hyper-volume. Here, it is used the improvement computed by a
                 * measure calculator (usually the R2(tch) fitness improvement
                 * rate).
                 */
                selection.creditAssignment(getImprovement());
                // 7. hi <- SelectMetaheuristic(P, A);
                selected = selection.getNext();
                selected.init(popcurr);
            }

            // 4. Popcurr <- Replace(Popcurr, Popnext);
            for (S s : popnext) {
                popcurr.add((S) s.copy());

            }
        }
    }

    @Override
    public List<S> getResult() {
        return SolutionListUtils.getNondominatedSolutions(popcurr);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Learning Automata based Hyper-heuristic";
    }

    /**
     * Section III.D. Switching to Another Meta-heuristic
     *
     * @param popcurr
     * @param popnext
     * @return
     */
    private boolean hasImprovement(List<S> popcurr, List<S> popnext) {
        setImprovement(calculator.computeFitnessImprovementRate(popcurr, popnext));
        return (getImprovement() > deltaV);
    }

    public double getImprovement() {
        return improvement;
    }

    public void setImprovement(double improvement) {
        this.improvement = improvement;
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

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public List<S> getPopcurr() {
        return popcurr;
    }

    public void setPopcurr(List<S> popcurr) {
        this.popcurr = popcurr;
    }

    public double getDeltaV() {
        return deltaV;
    }

    public void setDeltaV(double deltaV) {
        this.deltaV = deltaV;
    }

}
