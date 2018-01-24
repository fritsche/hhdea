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
package br.ufpr.inf.cbio.hhdea.algorithm.HHdEA;

import br.ufpr.inf.cbio.hhdea.algorithm.NSGAII.CONSGAIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAIII.CONSGAIIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2.COSPEA2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA.COThetaDEAConfiguration;
import br.ufpr.inf.cbio.hhdea.config.AlgorithmConfiguration;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class HHdEAConfiguration<S extends Solution> implements AlgorithmConfiguration<Algorithm<S>> {

    @Override
    public Algorithm<S> cofigure(Problem problem, int popSize, int generations) {

        setup();

        return new HHdEABuilder<>(problem)
                // .addAlgorithm(new CONSGAIIConfiguration().cofigure(problem, 0, 0))
                // .addAlgorithm(new CONSGAIIIConfiguration().cofigure(problem, 0, 0))
                // .addAlgorithm(new COSPEA2Configuration().cofigure(problem, 0, 0))
                .addAlgorithm(new COThetaDEAConfiguration().cofigure(problem, 0, 0))
                .setMaxEvaluations(popSize * generations)
                .setPopulationSize(popSize).build();
    }

    @Override
    public void setup() {

    }

}
