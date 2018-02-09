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

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class MetricsEvaluator<S extends Solution<?>> {

    private final int t;
    private List<S> initialPopulation;
    private List<S> lastPopulation;
    private final double[][] lambda;
    private final DominanceComparator comparator = new DominanceComparator();
    protected double[] zp_; 	// ideal point for Pareto-based population
    protected int m;

    public enum Metrics {
        R2IMPROVEMENT, DOMINANCERATIO, IMPROVEMENTCOUNT, PBIDIFFERENCE//, IMPROVEMENTDISTANCE
    };

    private double[][] metrics;

    public double getMetric(int moea, Metrics metric) {
        return metrics[moea][metric.ordinal()];
    }

    public double[][] getMetrics() {
        return metrics;
    }

    public void setMetrics(double[][] metrics) {
        this.metrics = metrics;
    }

    public MetricsEvaluator(int numberOfMOEAs, List<List<S>> population, double[][] lambda, int m) {
        this.m = m;
        this.zp_ = new double[m]; // ideal point for Pareto-based population
        initIdealPoint();
        this.t = numberOfMOEAs;
        this.metrics = new double[this.t][Metrics.values().length];
        this.lambda = lambda;
        this.initialPopulation = new ArrayList<>();
        this.lastPopulation = new ArrayList<>(t);
        population.forEach((l) -> {
            initialPopulation.addAll(l);
            lastPopulation.addAll(l);
            l.forEach((p) -> {
                updateReference(p, zp_);
            });
        });
    }

    public void updateReference(Solution indiv, double[] z_) {
        for (int i = 0; i < m; i++) {
            if (indiv.getObjective(i) < z_[i]) {
                z_[i] = indiv.getObjective(i);
            }
        }
    } // updateReference

    private void initIdealPoint() {
        for (int i = 0; i < m; i++) {
            zp_[i] = 1.0e+30;
        }
    } // initIdealPoint

    /**
     * Fitness Landscape Analysis (FLA) inspired metrics for MOP.
     *
     * @param parents
     * @param offspring
     */
    public void extractMetrics(List<List<S>> parents, List<List<S>> offspring) {

        Front lastFront = new ArrayFront(lastPopulation);
        parents.forEach((l) -> {
            l.forEach((p) -> {
                updateReference(p, zp_);
            });
        });
        offspring.forEach((l) -> {
            l.forEach((p) -> {
                updateReference(p, zp_);
            });
        });

        for (int moea = 0; moea < offspring.size(); moea++) {

            if (offspring.get(moea).isEmpty()) {
                for (Metrics metric : Metrics.values()) {
                    metrics[moea][metric.ordinal()] = 0.0;
                }
                continue;
            }

            List<S> union = new ArrayList<>(); // parents and offspring
            union.addAll(parents.get(moea));
            union.addAll(offspring.get(moea));
            List<S> all = new ArrayList<>();
            all.addAll(union);
            all.addAll(initialPopulation);
            Front referenceFront = new ArrayFront(all);
            Front initialFront = new ArrayFront(initialPopulation);
            Front moeaFront = new ArrayFront(union);
            R2 r2 = new R2(lambda, referenceFront);
            double r2initialFront = r2.r2(initialFront);
            double r2moeaFront = r2.r2(moeaFront);
            double r2lastFront = r2.r2(lastFront);
            /**
             * R2IMPROVEMENT. Difference between current R2 and the R2 of the
             * initial population. (Inspired on the FLA concept of
             * *Searchability*).
             */
            double landmarking = ((r2initialFront - r2moeaFront) / (r2initialFront));
            metrics[moea][Metrics.R2IMPROVEMENT.ordinal()] = landmarking;
            /**
             * IMPROVEMENTCOUNT. How many iterations since the previous R2 was
             * best or equal the current one. (Inspired on the FLA concept of
             * *Adaptive Walk*).
             */
            double adaptivewalk = 0;
            if (r2moeaFront < r2lastFront) {
                adaptivewalk = metrics[moea][Metrics.IMPROVEMENTCOUNT.ordinal()] + 1;
            }
            metrics[moea][Metrics.IMPROVEMENTCOUNT.ordinal()] = adaptivewalk;
            /**
             * DOMINANCERATIO. Percentage of parents dominated by offspring.
             * (Inspired on the FLA concept of *Evolvability*).
             */
            int count = 0;
            for (S p : parents.get(moea)) {
                for (S o : offspring.get(moea)) {
                    if (comparator.compare(p, o) == 1) {
                        count++;
                        break;
                    }
                }
            }
            metrics[moea][Metrics.DOMINANCERATIO.ordinal()] = count / (double) parents.get(moea).size();
            /**
             * PBIDIFFERENCE. Difference between the average PBI of the current
             * population and the average PBI from the previous one. (Inspired
             * on the FLA concept of *Evolvability*).
             */
            double uavg = .0;
            for (S u : union) {
                double min = PBI(u, lambda[0]);
                for (int w = 1; w < lambda.length; w++) {
                    double pbi = PBI(u, lambda[w]);
                    if (pbi < min) {
                        min = pbi;
                    }
                }
                uavg += min;
            }
            uavg /= union.size();
            double pavg = .0;
            for (S p : lastPopulation) {
                double min = PBI(p, lambda[0]);
                for (int w = 1; w < lambda.length; w++) {
                    double pbi = PBI(p, lambda[w]);
                    if (pbi < min) {
                        min = pbi;
                    }
                }
                pavg += min;
            }
            pavg /= lastPopulation.size();
            metrics[moea][Metrics.PBIDIFFERENCE.ordinal()] = (pavg - uavg) / pavg;
        }
        lastPopulation.clear();
        parents.forEach((p) -> {
            lastPopulation.addAll(p);
        });
        offspring.forEach((o) -> {
            lastPopulation.addAll(o);
        });

    }

    public void log() {
        for (Metrics metric : Metrics.values()) {
            System.out.print(metric + ":\t");
            for (int moea = 0; moea < t; moea++) {
                System.out.print(metrics[moea][metric.ordinal()] + "\t");
            }
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
