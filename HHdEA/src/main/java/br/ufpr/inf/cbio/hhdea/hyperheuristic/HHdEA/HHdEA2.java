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
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRateCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public class HHdEA2<S extends Solution<?>> extends HHdEA<S> {

    public HHdEA2(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxEvaluations,
            Problem problem, String name, SelectionFunction<CooperativeAlgorithm> selection,
            FitnessImprovementRateCalculator fir) {
        super(algorithms, populationSize, maxEvaluations, problem, name, selection, fir);
    }

    private Map<CooperativeAlgorithm<S>, List<S>> copyPopulations() {
        Map<CooperativeAlgorithm<S>, List<S>> populations = new HashMap(algorithms.size());
        for (CooperativeAlgorithm<S> algorithm : algorithms) {
            List<S> population = new ArrayList<>(algorithm.getPopulation().size());
            for (S s : algorithm.getPopulation()) {
                population.add((S) s.copy());
            }
            populations.put(algorithm, population);
        }
        return populations;
    }

    private Map<CooperativeAlgorithm<S>, Double> computeImprovementOfAllMOEAs(Map<CooperativeAlgorithm<S>, List<S>> populations) {
        Map<CooperativeAlgorithm<S>, Double> moeasfir = new HashMap<>(algorithms.size());;
        for (Map.Entry<CooperativeAlgorithm<S>, List<S>> entry : populations.entrySet()) {
            CooperativeAlgorithm<S> algorithm = entry.getKey();
            List<S> oldpop = entry.getValue();
            List<S> newpop = algorithm.getPopulation();
            moeasfir.put(algorithm,
                    calculator.computeFitnessImprovementRate(oldpop, newpop));
        }
        return moeasfir;
    }

    @Override
    public void run() {

        evaluations = 0;
        for (CooperativeAlgorithm<S> alg : algorithms) {
            alg.init(populationSize);
            evaluations += alg.getPopulation().size();
            selection.add(alg);
        }
        selection.init();

        while (!isStoppingConditionReached()) {

            // copy the population of every MOEA
            Map<CooperativeAlgorithm<S>, List<S>> populations = copyPopulations();

            // heuristic selection
            CooperativeAlgorithm<S> selected = selection.getNext();
            setSelectedHeuristic(selected); // save to notify observers
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

            // compute the improvement of all MOEAs (old vs new pop)
            Map<CooperativeAlgorithm<S>, Double> moeasfir = computeImprovementOfAllMOEAs(populations);

            // extract metrics (parents vs offspring [solutions generated this iteration])
            setFir(calculator.computeFitnessImprovementRate(parents, offspring));

            // compute reward
            selection.creditAssignment(getFir());

            // move acceptance
            // ALL MOVES
            setChanged();
            notifyObservers();
        }

    }

}
