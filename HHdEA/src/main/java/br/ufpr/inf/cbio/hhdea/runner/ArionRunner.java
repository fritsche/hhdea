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
package br.ufpr.inf.cbio.hhdea.runner;

import br.ufpr.inf.cbio.hhdea.config.AlgorithmConfigurationFactory;
import br.ufpr.inf.cbio.hhdea.problem.ProblemFactory;
import br.ufpr.inf.cbio.hhdea.util.OutputUtils;
import java.util.List;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class ArionRunner extends AbstractAlgorithmRunner {

    public static void main(String[] args) {

        // do not print info
        JMetalLogger.logger.setLevel(Level.WARNING);

        if (args.length != 6) {
            throw new JMetalException("Needed arguments: "
                    + "outputDirectory algorithm problem m id seed");
        }

        // parse arguments
        int i = 0;
        String experimentBaseDirectory = args[i++];
        String algorithmName = args[i++];
        String problemName = args[i++];
        int m = Integer.parseInt(args[i++]);
        int id = Integer.parseInt(args[i++]);
        int seed = Integer.parseInt(args[i++]);
        int generations = getGenerationsNumber(problemName, m);
        int popSize = getPopSize(problemName);

        // set seed
        JMetalRandom.getInstance().setSeed(seed);

        Algorithm<List<DoubleSolution>> algorithm = AlgorithmConfigurationFactory
                .getAlgorithmConfiguration(algorithmName)
                .configure(ProblemFactory.getProblem(problemName, m), popSize, generations);
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

    public static int getGenerationsNumber(String problem, int m) {
        return 100;
    }

    public static int getPopSize(String problem) {
        if (problem.equals("DTLZ1") || problem.equals("DTLZ3")) {
            return 256;
        }
        return 128;
    }
}
