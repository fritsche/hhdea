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

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public interface CooperativeAlgorithm<S extends Solution<?>> {

    /**
     * Given an initial population and a maxNumber of fitness evaluations,
     * evolve this population and return the population evolved.
     *
     * @param initialPopulation
     * @param popSize
     * @param lambda
     * @param extremeSolutions the best and worse solution known for each objective. Used to
     * compute ideal and nadir points.
     * @return
     */
    public List<S> run(List<S> initialPopulation, int popSize, double lambda[][], List<S> extremeSolutions);

}
