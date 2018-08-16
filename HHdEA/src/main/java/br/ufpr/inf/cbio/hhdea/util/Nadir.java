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
package br.ufpr.inf.cbio.hhdea.util;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class Nadir<S extends Solution> {

    private final S reference;

    public Nadir(Problem problem) {
        reference = (S) problem.createSolution();
        setValue(reference, problem);
    }

    public S getReference() {
        return reference;
    }

    private void setValue(S reference, Problem problem) {
        if (problem.getName().equals((new DTLZ1()).getName())) {
            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                reference.setObjective(i, 1.0);
            }
        } else if ((problem.getName().equals((new DTLZ2()).getName()))
                || (problem.getName().equals((new DTLZ3()).getName()))
                || (problem.getName().equals((new DTLZ4()).getName()))) {
            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                reference.setObjective(i, 2.0);
            }
        } else if (problem.getName().startsWith("WFG")) {
            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                reference.setObjective(i, (2 * (i + 1)) + 1);
            }
        } else {
            throw new JMetalException("There is no configurations for " + problem + " reference point (nadir).");
        }
    }

}
