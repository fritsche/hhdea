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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.qualityindicator.impl.R2;
import org.uma.jmetal.solution.Solution;
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
    private String file;

    public enum Metrics {
        LANDMARKING, EVOLVABILITY, ADAPTIVE_WALK
    };

    private double[][] metrics;

    public MetricsEvaluator(int numberOfMOEAs, List<List<S>> population, String file) {
        this.t = numberOfMOEAs;
        this.metrics = new double[this.t][Metrics.values().length];
        this.file = file;

        this.initialPopulation = new ArrayList<>();
        this.lastPopulation = new ArrayList<>(t);
        population.forEach((l) -> {
            initialPopulation.addAll(l);
            lastPopulation.add(new ArrayList<>(l));
        });
    }

    public void extractMetrics(List<List<S>> parents, List<List<S>> offspring) {

        for (int moea = 0; moea < t; moea++) {
            try {
                List<S> union = new ArrayList<>(); // parents and offspring
                union.addAll(parents.get(moea));
                union.addAll(offspring.get(moea));
                List<S> all = new ArrayList<>();
                all.addAll(union);
                all.addAll(initialPopulation);
                ArrayFront referenceFront = new ArrayFront(all);
                ArrayFront initialFront = new ArrayFront(initialPopulation);
                ArrayFront moeaFront = new ArrayFront(union);
                ArrayFront lastFront = new ArrayFront(lastPopulation.get(moea));
                R2 r2 = new R2(file, referenceFront);
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
                 * Adaptive walk. How many iterations since the previous r2 was
                 * best or equal the current one.
                 */

            } catch (IOException ex) {
                Logger.getLogger(MetricsEvaluator.class.getName()).log(Level.SEVERE, "Weight Vectors file not found [" + file + "]!", ex);
            }

        }

    }

}
