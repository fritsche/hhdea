package br.ufpr.inf.cbio.hhdea.runner;

import br.ufpr.inf.cbio.hhdea.config.AlgorithmConfigurationFactory;
import br.ufpr.inf.cbio.hhdea.problem.ProblemFactory;
import br.ufpr.inf.cbio.hhdea.runner.methodology.ArionMethodology;
import br.ufpr.inf.cbio.hhdea.runner.methodology.MaFMethodology;
import br.ufpr.inf.cbio.hhdea.runner.methodology.Methodology;
import br.ufpr.inf.cbio.hhdea.runner.methodology.NSGAIIIMethodology;
import br.ufpr.inf.cbio.hhdea.util.OutputUtils;
import java.util.List;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

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
/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class MainRunner {

    public static void main(String[] args) {

        JMetalLogger.logger.setLevel(Level.ALL);

        if (args.length != 7) {
            throw new JMetalException("Needed arguments: "
                    + "methodologyName outputDirectory algorithm problem m id seed");
        }

        // parse arguments
        int i = 0;
        String methodologyName = args[i++];
        String experimentBaseDirectory = args[i++];
        String algorithmName = args[i++];
        String problemName = args[i++];
        int m = Integer.parseInt(args[i++]);
        int id = Integer.parseInt(args[i++]);
        int seed = Integer.parseInt(args[i++]);

        Problem problem = ProblemFactory.getProblem(problemName, m);

        Methodology methodology = null;
        if (methodologyName.equals(NSGAIIIMethodology.class.getSimpleName())) {
            methodology = new NSGAIIIMethodology(problemName, m);
        } else if (methodologyName.equals(MaFMethodology.class.getSimpleName())) {
            methodology = new MaFMethodology(m, problem.getNumberOfVariables());
        } else if (methodologyName.equals(ArionMethodology.class.getSimpleName())) {
            methodology = new ArionMethodology(problemName);
        } else {
            throw new JMetalException("There is no configuration for " + methodologyName + " methodology.");
        }

        int maxFitnessevaluations = methodology.getMaxFitnessEvaluations();
        int popSize = methodology.getPopulationSize();

        // set seed
        JMetalRandom.getInstance().setSeed(seed);

        Algorithm<List<DoubleSolution>> algorithm = AlgorithmConfigurationFactory
                .getAlgorithmConfiguration(algorithmName)
                .configure(popSize, maxFitnessevaluations, problem);

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        OutputUtils outputUtils = new OutputUtils(experimentBaseDirectory);
        outputUtils.prepareOutputDirectory();

        List population = algorithm.getResult();
        new SolutionListOutput(population).setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext(experimentBaseDirectory + "VAR" + id + ".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext(experimentBaseDirectory + "FUN" + id + ".tsv"))
                .print();
        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.log(Level.INFO, "Total execution time: {0}ms", computingTime);

    }
}
