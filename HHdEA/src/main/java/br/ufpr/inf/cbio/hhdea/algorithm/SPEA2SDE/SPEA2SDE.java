/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche at gmail.com>
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
package br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE;

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
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class SPEA2SDE<S extends Solution> implements Algorithm<List<S>> {

    public static final int TOURNAMENTS_ROUNDS = 1;
    String step_path = null;
    int step_value = 0;
    int populationSize, archiveSize, maxEvaluations, evaluations;
    CrossoverOperator<S> crossoverOperator;
    MutationOperator<S> mutationOperator;
    SelectionOperator<List<S>, S> selectionOperator;
    List<S> solutionSet, archive, offSpringSolutionSet;

    protected final Problem<S> problem_;

    public SPEA2SDE(SPEA2SDEBuilder<S> builder) {
        problem_ = builder.getProblem();
        populationSize = builder.getPopulationSize();
        archiveSize = builder.getArchiveSize();
        maxEvaluations = builder.getMaxEvaluations();
        crossoverOperator = builder.getCrossover();
        mutationOperator = builder.getMutation();
        selectionOperator = builder.getSelection();
    }

    @Override
    public void run() {
        solutionSet = new ArrayList<>(populationSize);
        archive = new ArrayList<>(archiveSize);
        evaluations = 0;

        for (int i = 0; i < populationSize; i++) {
            S newSolution = problem_.createSolution();
            problem_.evaluate(newSolution);
            evaluations++;
            solutionSet.add(newSolution);
        }

        while (evaluations < maxEvaluations) {
            List<S> union = new ArrayList<>(populationSize + archiveSize);
            union.addAll(solutionSet);
            union.addAll(archive);

            SPEA2SDEFitness spea = new SPEA2SDEFitness(union);
            spea.fitnessAssign();
            archive = spea.environmentalSelection(archiveSize);
            // Create a new offspringPopulation
            offSpringSolutionSet = new ArrayList<>(populationSize);
            List<S> parents = new ArrayList<>();
            while (offSpringSolutionSet.size() < populationSize) {
                int j = 0;
                do {
                    j++;
                    parents.add(selectionOperator.execute(archive));
                } while (j < SPEA2SDE.TOURNAMENTS_ROUNDS); // do-while
                int k = 0;
                do {
                    k++;
                    parents.add(selectionOperator.execute(archive));
                } while (k < SPEA2SDE.TOURNAMENTS_ROUNDS); // do-while

                // make the crossover
                List<S> offSpring = crossoverOperator.execute(parents);
                mutationOperator.execute(offSpring.get(0));
                problem_.evaluate(offSpring.get(0));
                offSpringSolutionSet.add(offSpring.get(0));
                evaluations++;
            } // while
            // End Create a offSpring solutionSet
            solutionSet = offSpringSolutionSet;

        } // while

    }

    @Override
    public List<S> getResult() {
        return SolutionListUtils.getNondominatedSolutions(solutionSet);
    }

    @Override
    public String getName() {
        return "SPEA2SDE";
    }

    @Override
    public String getDescription() {
        return "SPEA2 with Shift-Based Density Estimation";
    }

}
