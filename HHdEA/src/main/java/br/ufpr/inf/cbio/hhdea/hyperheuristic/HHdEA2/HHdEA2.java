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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA2;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.CooperativeAlgorithm;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA.HHdEA;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.selection.SelectionFunction;
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRateCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public class HHdEA2<S extends Solution<?>> extends HHdEA<S> {

    private Map<CooperativeAlgorithm<S>, Double> moeasfir;

    public HHdEA2(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxEvaluations,
            Problem problem, String name, SelectionFunction<CooperativeAlgorithm> selection,
            FitnessImprovementRateCalculator fir) {
        super(algorithms, populationSize, maxEvaluations, problem, name, selection, fir);
    }

    private Map<CooperativeAlgorithm<S>, List<S>> copyPopulations() {
        Map<CooperativeAlgorithm<S>, List<S>> populations = new HashMap(getAlgorithms().size());
        for (CooperativeAlgorithm<S> algorithm : getAlgorithms()) {
            List<S> population = new ArrayList<>(algorithm.getPopulation().size());
            for (S s : algorithm.getPopulation()) {
                population.add((S) s.copy());
            }
            populations.put(algorithm, population);
        }
        return populations;
    }

    private void computeImprovementOfAllMOEAs(Map<CooperativeAlgorithm<S>, List<S>> populations) {
        moeasfir = new HashMap<>(getAlgorithms().size());
        for (Map.Entry<CooperativeAlgorithm<S>, List<S>> entry : populations.entrySet()) {
            CooperativeAlgorithm<S> algorithm = entry.getKey();
            List<S> oldpop = entry.getValue();
            List<S> newpop = algorithm.getPopulation();
            moeasfir.put(algorithm,
                    calculator.computeFitnessImprovementRate(oldpop, newpop));
        }
    }

    @Override
    public void run() {

        setEvaluations(0);
        for (CooperativeAlgorithm<S> alg : getAlgorithms()) {
            alg.init(populationSize);
            setEvaluations(getEvaluations() + alg.getPopulation().size());
            selection.add(alg);
        }
        selection.init();

        while (getEvaluations() < getMaxEvaluations()) {

            JMetalLogger.logger.log(Level.FINE, "Progress: {0}",
                    String.format("%.2f%%", getEvaluations() / (double) getMaxEvaluations() * 100.0));

            // copy the population of every MOEA
            Map<CooperativeAlgorithm<S>, List<S>> populations = copyPopulations();

            // heuristic selection
            CooperativeAlgorithm<S> selected = selection.getNext(getEvaluations() / populationSize);
            // set selected to be logged
            setSelected(selected);
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
                setEvaluations(getEvaluations() + 1);
            }

            // cooperation phase
            for (CooperativeAlgorithm<S> neighbor : getAlgorithms()) {
                if (neighbor != selected) {
                    List<S> migrants = new ArrayList<>();
                    for (S s : offspring) {
                        migrants.add((S) s.copy());
                    }
                    neighbor.receive(migrants);
                }
            }

            // compute the improvement of all MOEAs (old vs new pop)
            computeImprovementOfAllMOEAs(populations);

            // extract metrics (parents vs offspring [solutions generated this iteration])
            setFir(calculator.computeFitnessImprovementRate(parents, offspring));

            // compute reward
            selection.creditAssignment(getFir());

            // move acceptance
            // ALL MOVES
            // notify observers
            setChanged();
            notifyObservers();
        }

    }

    public Map<CooperativeAlgorithm<S>, Double> getMoeasfir() {
        return moeasfir;
    }

    public void setMoeasfir(Map<CooperativeAlgorithm<S>, Double> moeasfir) {
        this.moeasfir = moeasfir;
    }

}
