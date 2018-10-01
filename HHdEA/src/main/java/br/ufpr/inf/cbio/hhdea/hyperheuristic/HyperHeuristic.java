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
package br.ufpr.inf.cbio.hhdea.hyperheuristic;

import java.util.List;
import java.util.Observable;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

/**
 * Set of hyper-heuristic information that can be logged. Those information can
 * be accessed by observers.
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 * @param <S>
 */
public abstract class HyperHeuristic<S extends Solution<?>> extends Observable implements Algorithm<List<S>> {

    private double fir;
    private CooperativeAlgorithm<S> selectedHeuristic;
    protected List<CooperativeAlgorithm<S>> algorithms;
    private int[] count;

    public HyperHeuristic(List<CooperativeAlgorithm<S>> algorithms) {
        this.algorithms = algorithms;
        this.count = new int[algorithms.size()];
    }

    public double getFir() {
        return fir;
    }

    public void setFir(double fir) {
        this.fir = fir;
    }

    public abstract boolean isStoppingConditionReached();

    public CooperativeAlgorithm<S> getSelectedHeuristic() {
        return selectedHeuristic;
    }

    public void setSelectedHeuristic(CooperativeAlgorithm<S> selectedHeuristic) {
        this.selectedHeuristic = selectedHeuristic;
        count[algorithms.indexOf(selectedHeuristic)]++;
    }

    public List<CooperativeAlgorithm<S>> getAlgorithms() {
        return algorithms;
    }

    public int[] getCount() {
        return count;
    }

    public void setCount(int[] count) {
        this.count = count;
    }

}
