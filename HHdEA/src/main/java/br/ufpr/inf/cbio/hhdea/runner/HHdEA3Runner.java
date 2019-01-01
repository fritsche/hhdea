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
package br.ufpr.inf.cbio.hhdea.runner;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA3.HHdEA3;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA3.HHdEA3Configuration;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA3.observer.HHdEA3Logger;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA3.observer.MOEASFIRLogger;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA3.observer.SelectedMOEALogger;
import br.ufpr.inf.cbio.hhdea.problem.ProblemFactory;
import br.ufpr.inf.cbio.hhdea.runner.methodology.MaFMethodology;
import br.ufpr.inf.cbio.hhdea.runner.methodology.Methodology;
import br.ufpr.inf.cbio.hhdea.util.output.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Gian Fritsche <gian.fritsche at gmail.com>
 */
public class HHdEA3Runner {

    public static void help(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "java -cp <jar> br.ufpr.inf.cbio.hhdea.runner.HHdEA3Runner",
                "\nExecute a single independent run of the HHdEA algorithm on a given <problem>.\n",
                options,
                "\nPlease report issues at https://github.com/fritsche/hhdea/issues", true);
    }

    public static void main(String[] args) {
        try {
            JMetalLogger.logger.setLevel(Level.ALL);

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd;
            Options options = new Options();

            options.addOption(Option.builder("h").longOpt("help").desc("print this message and exits.").build());
            options.addOption(Option.builder("id").hasArg().argName("id")
                    .desc("set the independent run id, default 0.").build());
            options.addOption(Option.builder("s").longOpt("seed").hasArg().argName("seed")
                    .desc("set the seed for JMetalRandom, default System.currentTimeMillis()").build());
            options.addOption(Option.builder("p").longOpt("problem").hasArg().argName("problem")
                    .desc("set the problem instance: MaF[1-15], default MaF01").build());
            options.addOption(Option.builder("m").longOpt("objectives").hasArg().argName("objectives")
                    .desc("set the number of objectives (default value is 5).").build());
            options.addOption(Option.builder("P").longOpt("output-path").hasArg().argName("path")
                    .desc("directory path for output (if no path is given experiment/ will be used.)").build());

            // parse command line
            cmd = parser.parse(options, args);
            // print help and exit
            if (cmd.hasOption("h") || args.length == 0) {
                help(options);
                System.exit(0);
            }

            String problemName = "MaF01", aux;
            int m = 5;
            long seed = System.currentTimeMillis();
            String experimentBaseDirectory = "experiment/";
            String methodologyName = MaFMethodology.class.getSimpleName();
            int id = 0;
            String algorithmName = "HHdEA3";

            if ((aux = cmd.getOptionValue("p")) != null) {
                problemName = aux;
            }
            if ((aux = cmd.getOptionValue("P")) != null) {
                experimentBaseDirectory = aux;
            }
            if ((aux = cmd.getOptionValue("m")) != null) {
                m = Integer.parseInt(aux);
            }
            if ((aux = cmd.getOptionValue("id")) != null) {
                id = Integer.parseInt(aux);
            }
            if ((aux = cmd.getOptionValue("s")) != null) {
                seed = Integer.parseInt(aux);
            }

            Problem problem = ProblemFactory.getProblem(problemName, m);
            JMetalLogger.logger.log(Level.CONFIG, "Problem: {0} with {1} objectives", new Object[]{problemName, m});
            Methodology methodology = new MaFMethodology(m, problem.getNumberOfVariables());
            JMetalLogger.logger.log(Level.CONFIG, "Methodology: {0}", methodology.getClass().getSimpleName());
            int maxFitnessevaluations = methodology.getMaxFitnessEvaluations();
            JMetalLogger.logger.log(Level.CONFIG, "Max Fitness Evaluations: {0}", maxFitnessevaluations);
            int popSize = methodology.getPopulationSize();

            // set seed
            JMetalRandom.getInstance().setSeed(seed);
            JMetalLogger.logger.log(Level.CONFIG, "Seed: {0}", seed);

            HHdEA3 hhdea3 = new HHdEA3Configuration(algorithmName).configure(popSize, maxFitnessevaluations, problem);
            JMetalLogger.logger.log(Level.CONFIG, "Algorithm: {0}", hhdea3.getName());

            String outputfolder = experimentBaseDirectory + "/"
                    + methodologyName + "/"
                    + m
                    + "/output/"
                    + algorithmName + "/"
                    + problemName + "/";

            // create loggers
            List<HHdEA3Logger> loggers = new ArrayList<>();
            loggers.add(new MOEASFIRLogger(outputfolder, "moeasfir." + id));
            loggers.add(new SelectedMOEALogger(outputfolder, "selected." + id));

            // append loggers to algorithm
            for (HHdEA3Logger logger : loggers) {
                hhdea3.addObserver(logger);
            }

            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(hhdea3)
                    .execute();

            // close loggers (write to file)
            for (HHdEA3Logger logger : loggers) {
                logger.close();
            }

            long computingTime = algorithmRunner.getComputingTime();
            JMetalLogger.logger.log(Level.INFO, "Total execution time: {0}ms", computingTime);

            List population = SolutionListUtils.getNondominatedSolutions(hhdea3.getResult());

            int maxPopSize = 340; // MaFMethodology

            // prune output population size
            if (population.size() > maxPopSize) {
                population = MOEADUtils.getSubsetOfEvenlyDistributedSolutions(population, maxPopSize);
            }

            String datafolder = experimentBaseDirectory + "/"
                    + methodologyName + "/"
                    + m
                    + "/data/"
                    + algorithmName + "/"
                    + problemName + "/";

            Utils outputUtils = new Utils(datafolder);
            outputUtils.prepareOutputDirectory();

            new SolutionListOutput(population).setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(datafolder + "VAR" + id + ".tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext(datafolder + "FUN" + id + ".tsv"))
                    .print();

        } catch (ParseException ex) {
            Logger.getLogger(HHdEA3Runner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
