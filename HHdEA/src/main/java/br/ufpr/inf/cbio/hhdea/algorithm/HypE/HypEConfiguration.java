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
package br.ufpr.inf.cbio.hhdea.algorithm.HypE;

import br.ufpr.inf.cbio.hhdea.config.AlgorithmConfiguration;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.FitnessComparator;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public class HypEConfiguration<S extends Solution> implements AlgorithmConfiguration<HypE<?>> {

    private Problem<S> problem;
    private int bounds;
    private int samples;
    private CrossoverOperator crossoverOperator;
    private MutationOperator mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;

    @Override
    public HypE<?> configure(Problem problem, int popSize, int generations) {
        this.problem = problem;
        setup();
        return new HypEBuilder<>(problem).setBounds(bounds).setCrossoverOperator(crossoverOperator)
                .setMaxEvaluations(popSize * generations).setMutationOperator(mutationOperator).setPopulationSize(popSize)
                .setSamples(samples).setSelectionOperator(selectionOperator).build();
    }

    @Override
    public void setup() {
        this.bounds = 200;
        this.samples = 10000;
        double crossoverProbability = 1.0;
        double crossoverDistributionIndex = 20.0;
        crossoverOperator = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutationOperator = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
        selectionOperator = new BinaryTournamentSelection<>(new FitnessComparator<>());
    }

}
