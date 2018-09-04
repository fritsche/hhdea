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
package br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.traditional;

import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.CooperativeAlgorithm;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * An online learning hyper-heuristic selection based on low-level heursitics.
 * It follows the hyper-heuristic approach and the problem domain barrier from
 * the literature: (Burke, 2013) Burke EK, Gendreau M, Hyde M, Kendall G, Ochoa
 * G, Özcan E, Qu R (2013) Hyper-heuristics: A survey of the state of the art.
 * Journal of the Operational Research Society. (Cowling 2001) Cowling P,
 * Kendall G, Soubeiga E (2001) A hyper-heuristic approach to scheduling a sales
 * summit. In: Selected papers from the Third International Conference on
 * Practice and Theory of Automated Timetabling.
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class Traditional<S extends Solution<?>> implements Algorithm<List<S>> {

    protected List<CooperativeAlgorithm<S>> algorithms;
    private final int populationSize;
    private final int maxGenerations;
    private final Problem problem;

    public Traditional(List<CooperativeAlgorithm<S>> algorithms, int populationSize,
            int maxGenerations, Problem problem) {
        this.algorithms = algorithms;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.problem = problem;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns the non-dominated solutions from the last applied MOEA.
     *
     * @return
     */
    @Override
    public List<S> getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "An online learning hyper-heuristic selection based on low-level heursitics.";
    }

}
