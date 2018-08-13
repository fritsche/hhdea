/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche@gmail.com>
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
package br.ufpr.inf.cbio.hhdea.algorithm.GrEA;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/**
 *
 * @author Gian Fritsche <gian.fritsche@gmail.com>
 * @param <S>
 */
public class GrEA<S extends Solution> implements Algorithm<List<S>> {

    private final int populationSize;
    private final int maxEvaluations;
    private int evaluations;
    private final int div;
    private List<S> population;
    private List<S> offspringPopulation;
    private List<S> union;
    private final CrossoverOperator<S> crossoverOperator;
    private final MutationOperator<S> mutationOperator;
    private final SelectionOperator<List<S>, S> selectionOperator;
    protected final Problem<S> problem_;

    public GrEA(GrEABuilder builder) {
        problem_ = builder.getProblem();
        populationSize = builder.getPopulationSize();
        maxEvaluations = builder.getMaxEvaluations();
        div = builder.getDiv();
        mutationOperator = builder.getMutationOperator();
        crossoverOperator = builder.getCrossoverOperator();
        selectionOperator = builder.getSelectionOperator();
    }

    @Override
    public void run() {
        evaluations = 0;
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            S newSolution = problem_.createSolution();
            problem_.evaluate(newSolution);
            evaluations++;
            population.add(newSolution);
        }
        while (evaluations < maxEvaluations) {
            GrEAGrid grid = new GrEAGrid(population, div);
            grid.assign_GR_GCPD();
            grid.assign_GCD();
            offspringPopulation = new ArrayList<>(populationSize);
            for (int i = 0; i < (populationSize / 2); i++) {
                if (evaluations < maxEvaluations) {
                    List<S> parents = new ArrayList<>();
                    parents.add(selectionOperator.execute(population));
                    parents.add(selectionOperator.execute(population));

                    List<S> offSpring = crossoverOperator.execute(parents);

                    mutationOperator.execute(offSpring.get(0));
                    mutationOperator.execute(offSpring.get(1));

                    problem_.evaluate(offSpring.get(0));
                    problem_.evaluate(offSpring.get(1));
                    evaluations += 2;

                    offspringPopulation.add(offSpring.get(0));
                    offspringPopulation.add(offSpring.get(1));
                }
            }
            union = new ArrayList<>();
            union.addAll(population);
            union.addAll(offspringPopulation);
            environmentalSelection(union, population, div);
        }
    }

    @Override
    public List<S> getResult() {
        return SolutionListUtils.getNondominatedSolutions(population);
    }

    @Override
    public String getName() {
        return "GrEA";
    }

    @Override
    public String getDescription() {
        return "A Grid-Based Evolutionary Algorithm for Many-Objective Optimization";
    }

}
