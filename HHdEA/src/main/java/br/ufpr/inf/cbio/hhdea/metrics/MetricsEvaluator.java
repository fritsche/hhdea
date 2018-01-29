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
    private List<List<S>> lastPopulation;
    private final double[][] lambda;
    private final DominanceComparator comparator = new DominanceComparator();

    public enum Metrics {
        LANDMARKING, EVOLVABILITY, ADAPTIVE_WALK
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

    public MetricsEvaluator(int numberOfMOEAs, List<List<S>> population, double[][] lambda) {
        this.t = numberOfMOEAs;
        this.metrics = new double[this.t][Metrics.values().length];
        this.lambda = lambda;
        this.initialPopulation = new ArrayList<>();
        this.lastPopulation = new ArrayList<>(t);
        population.forEach((l) -> {
            initialPopulation.addAll(l);
            lastPopulation.add(new ArrayList<>(l));
        });
    }

    public void extractMetrics(List<List<S>> parents, List<List<S>> offspring) {

        for (int moea = 0; moea < offspring.size(); moea++) {

            if (offspring.get(moea).isEmpty()) {
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
            Front lastFront = new ArrayFront(lastPopulation.get(moea));
            R2 r2 = new R2(lambda, referenceFront);
            double r2initialFront = r2.r2(initialFront);
            double r2moeaFront = r2.r2(moeaFront);
            double r2lastFront = r2.r2(lastFront);
            /**
             * Landmarking. Difference between current R2 and the R2 of the
             * initial population.
             */
            double landmarking = ((r2initialFront - r2moeaFront) / (r2initialFront));
            metrics[moea][Metrics.LANDMARKING.ordinal()] = landmarking;
            /**
             * Adaptive walk. How many iterations since the previous R2 was best
             * or equal the current one.
             */
            double adaptivewalk = 0;
            if (r2moeaFront < r2lastFront) {
                adaptivewalk = metrics[moea][Metrics.ADAPTIVE_WALK.ordinal()] + 1;
            }
            metrics[moea][Metrics.ADAPTIVE_WALK.ordinal()] = adaptivewalk;
            lastPopulation.set(moea, new ArrayList<>(union));
            /**
             * Evolvability. Percentage of parents dominated by offspring.
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
            metrics[moea][Metrics.EVOLVABILITY.ordinal()] = count / (double) parents.get(moea).size();
        }
    }

    public void log() {
        for (Metrics m : Metrics.values()) {
            System.out.print(m + ":\t");
            for (int moea = 0; moea < t; moea++) {
                System.out.print(metrics[moea][m.ordinal()] + "\t");
            }
            System.out.println();
        }
    }

}
