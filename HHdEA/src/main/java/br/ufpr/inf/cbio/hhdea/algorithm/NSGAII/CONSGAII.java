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
package br.ufpr.inf.cbio.hhdea.algorithm.NSGAII;

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class CONSGAII<S extends Solution<?>> extends NSGAII implements CooperativeAlgorithm<S> {

    public CONSGAII(Problem problem, int maxEvaluations, int populationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, Comparator dominanceComparator, SolutionListEvaluator evaluator) {
        super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);
    }

    @Override
    public List<S> run(List<S> initialPopulation, int popSize, double[][] lambda, List<S> extremeSolutions) {

        List<S> offspringPopulation;
        List<S> matingPopulation;

        popSize += (popSize % 2);
        if (initialPopulation.size() < popSize) {
            initialPopulation.add((S) initialPopulation.get(JMetalRandom.getInstance().nextInt(0, popSize - 1)).copy());
        }

        // filter solutions
        maxPopulationSize = popSize;
        population = replacement(new ArrayList(0), initialPopulation);

        matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        population = replacement(population, offspringPopulation);

        return population;
    }

}
