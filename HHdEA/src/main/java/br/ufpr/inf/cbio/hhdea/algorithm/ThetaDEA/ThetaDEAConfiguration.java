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
package br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA;

import br.ufpr.inf.cbio.hhdea.config.AlgorithmConfiguration;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class ThetaDEAConfiguration implements AlgorithmConfiguration<DoubleSolution> {

    @Override
    public Algorithm cofigure(Problem<DoubleSolution> problem, int popSize, int generations) {

        ThetaDEABuilder builder = new ThetaDEABuilder(problem);

        builder.setCrossover(new SBXCrossover(1.0, 30.0))
                .setMutation(new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
                .setNormalize(true)
                .setTheta(5.0)
                .setMaxGenerations(generations)
                .setPopulationSize(popSize);

        return builder.build();
    }

}
