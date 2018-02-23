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

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class HHdEA<S extends Solution<?>> implements Algorithm<List<S>> {
    
    protected int maxGenerations;
    protected Problem<S> problem;
    protected List<S> population;
    protected List<S> extreme; // the best and worse known solutions for each objetive
    protected List<CooperativeAlgorithm<S>> algorithms;
    private final int populationSize;
    private double[][] lambda;
    private final String name;
    private MetricsEvaluator metrics;
    private int type;
    
    public HHdEA(List<CooperativeAlgorithm<S>> algorithms, int populationSize, int maxGenerations, Problem problem, String name, int type) {
        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.problem = problem;
        this.name = name;
        this.type = type;
    }
    
    @Override
    public void run() {
        
        initializeUniformWeight();
        
        initPopulation();
        initExtreme();
        this.metrics = new MetricsEvaluator(algorithms.size(), population, lambda, problem.getNumberOfObjectives());
        
        int active;
        
        for (int generations = 1; generations <= maxGenerations; generations++) {
            /**
             * MOEA selection
             */
            List<S> offspring = new ArrayList<>();
            active = 0;
            for (CooperativeAlgorithm alg : algorithms) {
                /**
                 * execute MOEA
                 */
                List<double[]> aux = new ArrayList<>();
                for (int i = active; i < lambda.length; i += algorithms.size()) {
                    aux.add(lambda[i]);
                }
                double[][] sublambda = new double[aux.size()][aux.get(0).length];
                for (int i = 0; i < aux.size(); i++) {
                    sublambda[i] = aux.get(i);
                }
                List<S> initial = new ArrayList<>(population);
                List output = alg.run(initial, sublambda.length, sublambda, extreme);
                offspring.addAll(output);
                /**
                 * Credit Assignment
                 */
                active++;
            }
            /**
             * Move Acceptance
             */
//            metrics.extractMetrics(null, offspring, 0);
//            metrics.log(0);
            
            population = offspring;
            updateExtreme();
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
     */
    protected void initPopulation() {
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            S newSolution = problem.createSolution();
            problem.evaluate(newSolution);
            population.add(newSolution);
        }
        
    }
    
    protected void updateExtreme() {
        for (int i = 0; i < populationSize; i++) {
            S sol = population.get(i);
            for (int m = 0; m < problem.getNumberOfObjectives(); m++) {
                int best = 2 * m;
                if (sol.getObjective(m) < extreme.get(best).getObjective(m)) {
                    extreme.set(best, sol);
                }
                int worse = best + 1;
                if (sol.getObjective(m) < extreme.get(worse).getObjective(m)) {
                    extreme.set(worse, sol);
                }
            }
        }
    }
    
    protected void initExtreme() {
        extreme = new ArrayList<>(problem.getNumberOfObjectives() * 2);
        S sol = population.get(0);
        for (int m = 0; m < problem.getNumberOfObjectives(); m++) {
            extreme.add(sol); // init best
            extreme.add(sol); // init worse
        }
        updateExtreme();
    }
    
    @Override
    public List<S> getResult() {
        return SolutionListUtils.getNondominatedSolutions(population);
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
