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
package br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.HHdEA;

import br.ufpr.inf.cbio.hhdea.algorithm.HypE.COHypEConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEAD.COMOEADConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEADD.COMOEADDConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOMBI2.COMOMBI2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAII.CONSGAIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAIII.CONSGAIIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2.COSPEA2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE.COSPEA2SDEConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA.COThetaDEAConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.CooperativeAlgorithm;
import br.ufpr.inf.cbio.hhdea.config.AlgorithmConfiguration;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.selection.CastroRoulette;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.selection.SelectionFunction;
import br.ufpr.inf.cbio.hhdea.metrics.fir.FitnessImprovementRate;
import br.ufpr.inf.cbio.hhdea.metrics.fir.R2ASFFIR;
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
    private SelectionFunction<CooperativeAlgorithm> selection;
    private FitnessImprovementRate fir;
    Problem problem;
    int popSize;

    public HHdEAConfiguration(String name) {
        this.name = name;
    }

    @Override
    public Algorithm<S> configure(Problem problem, int popSize, int generations) {

        this.problem = problem;
        this.popSize = popSize;

        setup();

        HHdEABuilder builder = new HHdEABuilder(problem);

        switch (name) {
            case "CONSGAII":
                builder.addAlgorithm(new CONSGAIIConfiguration().configure(problem, popSize, generations));
                break;
            case "CONSGAIII":
                builder.addAlgorithm(new CONSGAIIIConfiguration().configure(problem, popSize, generations));
                break;
            case "COSPEA2":
                builder.addAlgorithm(new COSPEA2Configuration().configure(problem, popSize, generations));
                break;
            case "COSPEA2SDE":
                builder.addAlgorithm(new COSPEA2SDEConfiguration().configure(problem, popSize, generations));
                break;
            case "COHypE":
                builder.addAlgorithm(new COHypEConfiguration().configure(problem, popSize, generations));
                break;
            case "COMOMBI2":
                builder.addAlgorithm(new COMOMBI2Configuration().configure(problem, popSize, generations));
                break;
            case "COThetaDEA":
                builder.addAlgorithm(new COThetaDEAConfiguration().configure(problem, popSize, generations));
                break;
            case "COMOEAD":
                builder.addAlgorithm(new COMOEADConfiguration().configure(problem, popSize, generations));
                break;
            case "COMOEADD":
                builder.addAlgorithm(new COMOEADDConfiguration().configure(problem, popSize, generations));
                break;
            default: // ALL
                builder
                        .addAlgorithm(new COSPEA2Configuration().configure(problem, popSize, generations))
                        .addAlgorithm(new COMOEADConfiguration().configure(problem, popSize, generations))
                        .addAlgorithm(new CONSGAIIConfiguration().configure(problem, popSize, generations))
                        .addAlgorithm(new COMOEADDConfiguration().configure(problem, popSize, generations))
                        .addAlgorithm(new COMOMBI2Configuration().configure(problem, popSize, generations))
                        .addAlgorithm(new CONSGAIIIConfiguration().configure(problem, popSize, generations))
                        .addAlgorithm(new COThetaDEAConfiguration().configure(problem, popSize, generations))
                        .addAlgorithm(new COSPEA2SDEConfiguration().configure(problem, popSize, generations))
                        .addAlgorithm(new COHypEConfiguration().configure(problem, popSize, generations));
        }

        return builder.setName(name).setSelection(selection).setFir(fir).
                setMaxGenerations(generations).setPopulationSize(popSize).build();
    }

    @Override
    public void setup() {
        this.selection = new CastroRoulette<>();
        this.fir = new R2ASFFIR(problem, popSize);
    }

}
