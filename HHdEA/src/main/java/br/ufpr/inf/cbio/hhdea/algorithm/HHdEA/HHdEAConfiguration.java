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

    private final String name;

    public HHdEAConfiguration(String name) {
        this.name = name;
    }

    @Override
    public Algorithm<S> configure(Problem problem, int popSize, int generations) {

        setup();

        HHdEABuilder builder = new HHdEABuilder(problem);

        switch (name) {
            case "CONSGAII":
                builder.addAlgorithm(new CONSGAIIConfiguration().configure(problem, 0, 0));
                break;
            case "CONSGAIII":
                builder.addAlgorithm(new CONSGAIIIConfiguration().configure(problem, 0, 0));
                break;
            case "COSPEA2":
                builder.addAlgorithm(new COSPEA2Configuration().configure(problem, 0, 0));
                break;
            case "COThetaDEA":
                builder.addAlgorithm(new COThetaDEAConfiguration().configure(problem, 0, 0));
                break;
            default: // ALL
                builder.addAlgorithm(new CONSGAIIConfiguration().configure(problem, 0, 0))
                        .addAlgorithm(new CONSGAIIIConfiguration().configure(problem, 0, 0))
                        .addAlgorithm(new COSPEA2Configuration().configure(problem, 0, 0))
                        .addAlgorithm(new COThetaDEAConfiguration().configure(problem, 0, 0));
        }

        return builder.setName(name)
                .setMaxGenerations(generations)
                .setPopulationSize(popSize).build();
    }

    @Override
    public void setup() {

    }

}
