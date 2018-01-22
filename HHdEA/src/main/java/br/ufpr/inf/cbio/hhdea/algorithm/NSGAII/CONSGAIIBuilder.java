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

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class CONSGAIIBuilder extends NSGAIIBuilder<Solution<?>> {

    public CONSGAIIBuilder(Problem<Solution<?>> problem, CrossoverOperator<Solution<?>> crossoverOperator, MutationOperator<Solution<?>> mutationOperator) {
        super(problem, crossoverOperator, mutationOperator);
    }

    @Override
    public CONSGAII<Solution<?>> build() {
        return new CONSGAII(getProblem(), getMaxIterations(), getPopulationSize(), 
                getCrossoverOperator(), getMutationOperator(), getSelectionOperator(), getSolutionListEvaluator());
    }

}
