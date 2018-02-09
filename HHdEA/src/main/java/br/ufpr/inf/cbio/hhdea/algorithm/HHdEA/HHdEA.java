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

import br.ufpr.inf.cbio.hhdea.metrics.MetricsEvaluator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 * @version 0.1 - Every MOEA uses a complete population - Decomposition based
 * MOEAs uses all Weight Vectors
 */
public class HHdEA<S extends Solution<?>> implements Algorithm<List<S>> {

    protected int maxGenerations;
    protected Problem<S> problem;
    protected List<List<S>> population;
    protected List<CooperativeAlgorithm<S>> algorithms;
    protected List<Integer> subpopsize;
    private final int populationSize;
    private double[][] lambda;
    private final String name;
    private MetricsEvaluator metrics;

    public HHdEA(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxGenerations, Problem problem, String name) {
        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.problem = problem;
        this.name = name;
    }

    @Override
    public void run() {

        initializeUniformWeight();

        population = new ArrayList<>(algorithms.size());
        int remainingPopulation = populationSize;
        float remainingQuota = 1.0f;
        subpopsize = new ArrayList<>(algorithms.size());

//        for (int moea = 0; moea < algorithms.size(); moea++) {
//            CooperativeAlgorithm alg = algorithms.get(moea);
//            alg.setQuota((float) (1.0 / (float) algorithms.size()));
//        }
        algorithms.get(0).setQuota(0); // NSGAII
        algorithms.get(1).setQuota(0); // NSGAIII
        algorithms.get(2).setQuota(0); // SPEA2
        algorithms.get(3).setQuota(0); // ThetaDEA

        JMetalRandom random = JMetalRandom.getInstance();
        int active = random.nextInt(0, algorithms.size() - 1);
        algorithms.get(active).setQuota(1);

        for (int moea = 0; moea < algorithms.size(); moea++) {
            CooperativeAlgorithm alg = algorithms.get(moea);
            subpopsize.add(alg.getPopulationSize(remainingPopulation, remainingQuota));
            remainingPopulation = remainingPopulation - subpopsize.get(moea);
            remainingQuota = remainingQuota - alg.getQuota();
            initPopulation(moea, subpopsize.get(moea));
        }

        this.metrics = new MetricsEvaluator(algorithms.size(), population, lambda);

        for (int generations = 1; generations <= maxGenerations; generations++) {

            /**
             * Generate offspring.
             */
            List<List<S>> offspring = new ArrayList<>();
            for (int moea = 0; moea < algorithms.size() && generations < maxGenerations; moea++) {
                CooperativeAlgorithm alg = algorithms.get(moea);
                offspring.add(alg.generateOffspring(population.get(moea), subpopsize.get(moea), lambda));
            }

            /**
             * Extract metrics.
             */
            metrics.extractMetrics(population, offspring);
            metrics.log();

            /**
             * Make decisions based on metrics and change quotas.
             */
            if (metrics.getMetric(active, MetricsEvaluator.Metrics.ADAPTIVE_WALK) == 0) {
                algorithms.get(active).setQuota(0);
                active = random.nextInt(0, algorithms.size() - 1);
                algorithms.get(active).setQuota(1);
            }

            /**
             * Update population.
             */
            remainingPopulation = populationSize;
            remainingQuota = 1.0f;
            for (int moea = 0; moea < algorithms.size(); moea++) {
                CooperativeAlgorithm alg = algorithms.get(moea);
                List<S> union = new ArrayList<>();
                population.forEach((pop) -> {
                    union.addAll(pop);
                });
                offspring.forEach((off) -> {
                    union.addAll(off);
                });
                subpopsize.set(moea, alg.getPopulationSize(remainingPopulation, remainingQuota));
                remainingPopulation = remainingPopulation - subpopsize.get(moea);
                remainingQuota = remainingQuota - alg.getQuota();
                population.set(moea, alg.environmentalSelection(union, subpopsize.get(moea), lambda));
            }
        }
    }

    protected void initializeUniformWeight() {
        String dataFileName;
        dataFileName = "W" + problem.getNumberOfObjectives() + "D_"
                + populationSize + ".dat";

        lambda = new double[populationSize][problem.getNumberOfObjectives()];

        try {
            InputStream in = getClass().getResourceAsStream("/WeightVectors/" + dataFileName);
            InputStreamReader isr = new InputStreamReader(in);
            try (BufferedReader br = new BufferedReader(isr)) {
                int i = 0;
                int j;
                String aux = br.readLine();
                while (aux != null) {
                    StringTokenizer st = new StringTokenizer(aux);
                    j = 0;
                    while (st.hasMoreTokens()) {
                        double value = new Double(st.nextToken());
                        lambda[i][j] = value;
                        j++;
                    }
                    aux = br.readLine();
                    i++;
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new JMetalException("initializeUniformWeight: failed when reading for file: /WeightVectors/" + dataFileName, e);
        }
    }

    /**
     * Initialize the population
     *
     * @param moea
     * @param popsize
     */
    protected void initPopulation(int moea, int popsize) {

        List<S> subpop = new ArrayList<>(popsize);

        for (int i = 0; i < popsize; i++) {
            S newSolution = problem.createSolution();
            problem.evaluate(newSolution);
            subpop.add(newSolution);
        }
        population.add(subpop);

    }

    @Override
    public List<S> getResult() {
        List<S> union = new ArrayList<>();
        population.forEach((pop) -> {
            union.addAll(pop);
        });
        return SolutionListUtils.getNondominatedSolutions(union);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Hyper-heuristics for distributed Evolutionary Algorithms";
    }

}
