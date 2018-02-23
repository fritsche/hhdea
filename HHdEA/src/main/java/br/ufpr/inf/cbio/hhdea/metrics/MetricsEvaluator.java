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
package br.ufpr.inf.cbio.hhdea.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class MetricsEvaluator<S extends Solution<?>> {

    private int populationSize;
    private Problem problem;
    private double[][] lambda;
    protected double[] zp_; 	// ideal point for Pareto-based population
    protected int m;
    protected R2 r2;

    public enum Metrics {
        OUTPUT_RAW_R2
    };

    private double[] metrics;

    public double getMetric(Metrics metric) {
        return metrics[metric.ordinal()];
    }

    public double[] getMetrics() {
        return metrics;
    }

    public void setMetrics(double[] metrics) {
        this.metrics = metrics;
    }

    public MetricsEvaluator(Problem problem, int populationSize) {
        this.m = problem.getNumberOfObjectives();
        this.problem = problem;
        this.populationSize = populationSize;
        this.zp_ = new double[m];
        this.metrics = new double[Metrics.values().length];
        initializeUniformWeight();

        Front reference = new ArrayFront(m, m);
        double val = 3.0;
        for (int i = 0; i < m; i++) {
            double[] values = new double[m];
            values[i] = val;
            val += 2.0;
            Point point = new ArrayPoint(values);
            reference.setPoint(i, point);
        }
        r2 = new R2(lambda, reference);
    }

    public void extractMetrics(List<S> solutions) {
        metrics[Metrics.OUTPUT_RAW_R2.ordinal()] = r2.evaluate(solutions);
    }

    public void log() {
        for (Metrics metric : Metrics.values()) {
            System.out.print(metric + ":\t");
            System.out.print(metrics[metric.ordinal()] + "\t");
            System.out.println();
        }
    }

    public double norm_vector(double[] z, int m) {
        double sum = 0;
        for (int i = 0; i < m; i++) {
            sum += z[i] * z[i];
        }
        return Math.sqrt(sum);
    }

    public double innerproduct(double[] vec1, double[] vec2) {
        double sum = 0;
        for (int i = 0; i < vec1.length; i++) {
            sum += vec1[i] * vec2[i];
        }
        return sum;
    }

    private void initializeUniformWeight() {
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

    public double PBI(Solution indiv, double[] lambda) {
        double fitness;

        double theta; // penalty parameter
        theta = 5.0;
        // normalize the weight vector (line segment)
        double nd = norm_vector(lambda, m);
        for (int i = 0; i < m; i++) {
            lambda[i] = lambda[i] / nd;
        }
        double[] realA = new double[m];
        double[] realB = new double[m];
        // difference between current point and reference point
        for (int n = 0; n < m; n++) {
            realA[n] = (indiv.getObjective(n) - zp_[n]);
        }   // distance along the line segment
        double d1 = Math.abs(innerproduct(realA, lambda));
        // distance to the line segment
        for (int n = 0; n < m; n++) {
            realB[n] = (indiv.getObjective(n) - (zp_[n] + d1 * lambda[n]));
        }
        double d2 = norm_vector(realB, m);
        fitness = d1 + theta * d2;
        return fitness;
    }

}
