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
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
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
public class Main {

    public static void main(String[] args) {

        JMetalLogger.logger.setLevel(Level.ALL);
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        Option h = Option.builder("h").longOpt("help")
                .desc("print this message").build();
        options.addOption(h);
        Option P = Option.builder("P").longOpt("output-path").hasArg().argName("path")
                .desc("directory path for output (if no path is given experiment/ will be used.)").build();
        options.addOption(P);
        Option M = Option.builder("M").longOpt("methodology").hasArg().argName("methodology")
                .desc("set the methodology to be used: NSGAIII (default), MaF, Arion.").build();
        options.addOption(M);
        Option a = Option.builder("a").longOpt("algorithm").hasArg().argName("algorithm").required()
                .desc("set the algorithm to be executed: NSGAII, MOEAD, MOEADD, ThetaDEA, NSGAIII, SPEA2, SPEA2SDE, HypE, MOMBI2, Traditional, <other>."
                        + "If <other> name is given, HHdEA will be executed and the algorithm output name will be <other>.").build();
        options.addOption(a);
        Option p = Option.builder("p").longOpt("problem").hasArg().argName("problem").required()
                .desc("set the problem instance: DTLZ[1-7], WFG[1-9], MinusDTLZ[1-7], MinusWFG[1-9], MaF[1-15]; "
                        + "--methodology must be set accordingly.").build();
        options.addOption(p);
        Option m = Option.builder("m").longOpt("objectives").hasArg().argName("objectives").required()
                .desc("set the number of objectives to <objectives>, --problem and --methodology must be set acordingly.").build();
        options.addOption(m);
        Option idoption = Option.builder("id").hasArg().argName("id")
                .desc("independent run id, default 0.").build();
        options.addOption(idoption);
        Option seedOption = Option.builder("s").longOpt("seed").hasArg().argName("seed")
                .desc("set the seed for JMetalRandom, default System.currentTimeMillis()").build();
        options.addOption(seedOption);

        String aux;
        String experimentBaseDirectory = "experiment/";
        String methodologyName = "NSGAIIIMethodology";
        String algorithmName;
        String problemName;
        int numberOfObjectives;
        int id = 0;
        long seed = System.currentTimeMillis();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(h.getOpt())) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("hhdea", options);
            }
            if ((aux = cmd.getOptionValue(P.getOpt())) != null) {
                experimentBaseDirectory = aux;
            }
            if ((aux = cmd.getOptionValue(M.getOpt())) != null) {
                methodologyName = aux + "Methodology";
            }
            if ((aux = cmd.getOptionValue(a.getOpt())) != null) {
                algorithmName = aux;
            } else if (a.isRequired()) {
                throw new JMetalException("Parameter -" + a.getOpt() + " is required");
            }
            if ((aux = cmd.getOptionValue(p.getOpt())) != null) {
                problemName = aux;
                if ((aux = cmd.getOptionValue(m.getOpt())) != null) {
                    problemName = aux;
                }
            } else if (p.isRequired()) {
                throw new JMetalException("Parameter -" + p.getOpt() + " is required");
            }
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        List population = SolutionListUtils.getNondominatedSolutions(algorithm.getResult());

        // final population size of MaF
        if (problem.getName().startsWith("MaF")) {
            popSize = 240;
        }

        // prune output population size
        if (population.size() > popSize) {
            population = MOEADUtils.getSubsetOfEvenlyDistributedSolutions(population, popSize);
        }

        String folder = experimentBaseDirectory + "/"
                + methodologyName + "/"
                + m
                + "/data/"
                + algorithmName + "/"
                + problemName + "/";

        OutputUtils outputUtils = new OutputUtils(folder);
        outputUtils.prepareOutputDirectory();

        new SolutionListOutput(population).setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext(folder + "VAR" + id + ".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext(folder + "FUN" + id + ".tsv"))
                .print();

        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.log(Level.INFO, "Total execution time: {0}ms", computingTime);

    }
}
