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
public class Runner {

    private String experimentBaseDirectory;
    private String methodologyName;
    private final String algorithmName;
    private final String problemName;
    private final int m;
    private int id;
    private long seed;

    private Algorithm<List<DoubleSolution>> algorithm;
    private Problem problem;
    private int popSize;

    public Runner(String algorithmName, String problemName, int m) {
        // required parameters
        this.algorithmName = algorithmName;
        this.problemName = problemName;
        this.m = m;

        // required parameters with default values
        this.experimentBaseDirectory = "experiment/";
        this.methodologyName = NSGAIIIMethodology.class.getSimpleName();
        this.id = 0;
        this.seed = System.currentTimeMillis();
    }

    public Runner(String experimentBaseDirectory, String methodologyName,
            String algorithmName, String problemName, int m, int id, long seed) {
        this.experimentBaseDirectory = experimentBaseDirectory;
        this.methodologyName = methodologyName;
        this.algorithmName = algorithmName;
        this.problemName = problemName;
        this.m = m;
        this.id = id;
        this.seed = seed;
    }

    public void run() {

        problem = ProblemFactory.getProblem(problemName, m);

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
        popSize = methodology.getPopulationSize();

        // set seed
        JMetalRandom.getInstance().setSeed(seed);

        algorithm = AlgorithmConfigurationFactory
                .getAlgorithmConfiguration(algorithmName)
                .configure(popSize, maxFitnessevaluations, problem);

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.log(Level.INFO, "Total execution time: {0}ms", computingTime);

    }

    public void printResult() {

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
    }

    public Runner setId(int id) {
        this.id = id;
        return this;
    }

    public Runner setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public Runner setExperimentBaseDirectory(String experimentBaseDirectory) {
        this.experimentBaseDirectory = experimentBaseDirectory;
        return this;
    }

    public Runner setMethodologyName(String methodologyName) {
        this.methodologyName = methodologyName;
        return this;
    }

}
