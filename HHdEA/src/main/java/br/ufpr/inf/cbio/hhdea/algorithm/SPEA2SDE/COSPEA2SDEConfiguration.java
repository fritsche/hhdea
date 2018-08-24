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
package br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE;

import org.uma.jmetal.problem.Problem;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class COSPEA2SDEConfiguration extends SPEA2SDEConfiguration {

    @Override
    public COSPEA2SDE configure(Problem problem, int popSize, int generations) {

        this.problem = problem;

        setup();

        return (new COSPEA2SDEBuilder<>(problem, crossoverOperator, mutationOperator)).setSelectionOperator(selectionOperator).setMaxIterations(generations).setPopulationSize(popSize).build();

    }

}