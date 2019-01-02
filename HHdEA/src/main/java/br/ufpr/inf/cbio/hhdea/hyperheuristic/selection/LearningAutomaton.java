/*
 * Copyright (C) 2019 Gian Fritsche <gmfritsche at inf.ufpr.br>
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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.selection;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * Li, W., Ozcan, E., & John, R. (2017). A Learning Automata based
 * Multiobjective Hyper-heuristic. IEEE Transactions on Evolutionary
 * Computation, (c), 1–15. https://doi.org/10.1109/TEVC.2017.2785346
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <T>
 */
public class LearningAutomaton<T> extends SelectionFunction<T> {

    /**
     * Transition probability matrix
     */
    private double[][] p;
    /**
     * Number of MOEAS
     */
    private int r;

    /**
     * Estimated action value matrix
     */
    private double[][] q;

    /**
     * Previously applied heuristic
     */
    private int i;
    /**
     * Currently applied heuristic
     */
    private int j;

    private final JMetalRandom random;
    private double alpha = 0.1;
    private double[][] lambda;
    private double m;

    public LearningAutomaton(double m) {
        random = JMetalRandom.getInstance();
        this.m = m;
    }

    @Override
    public void init() {
        r = lowlevelheuristics.size();
        p = new double[r][r];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < r; j++) {
                p[i][j] = 1.0 / (double) r;
            }
        }
        q = new double[r][r]; // initialized with zeros
        lambda = new double[r][r];
        updateSelected(random.nextInt(0, r - 1)); // set a random heuristic as current before start
    }

    @Override
    public T getNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Section III.B. Reinforcement Learning Scheme
     *
     * @param reward
     */
    @Override
    public void creditAssignment(double reward) {
        // Equation 9
        q[i][j] = q[i][j] + alpha * (reward - q[i][j]);
        // Equation 11
        lambda[i][j] = 0.1 + m * q[i][j];
        lambda[i][j] = Math.max(lambda[i][j], 0.0);
        lambda[i][j] = Math.min(lambda[i][j], 1.0);

        int beta = reward > 0.0 ? 1 : 0;

        // Equation 7
        p[i][j] = p[i][j] + lambda[i][j] * beta * (1 - p[i][j]) - lambda[i][j] * (1 - beta) * p[i][j];

        for (int l = 0; l < r; l++) {
            if (l != j) {
                // Equation 8
                p[i][l] = p[i][l] - lambda[i][l] * beta * p[i][l] + lambda[i][l] * (1 - beta) * (1 / (r - 1) - p[i][l]);
            }
        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateSelected(int selected) {
        this.i = this.j;
        this.j = selected;
        this.s = this.j;
    }

}
