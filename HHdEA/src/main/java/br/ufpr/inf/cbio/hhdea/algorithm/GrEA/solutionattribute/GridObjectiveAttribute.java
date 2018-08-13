/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche@gmail.com>
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
package br.ufpr.inf.cbio.hhdea.algorithm.GrEA.solutionattribute;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 *
 * @author Gian Fritsche <gian.fritsche@gmail.com>
 * @param <S>
 */
public class GridObjectiveAttribute<S extends Solution<?>> extends GenericSolutionAttribute<S, List<Integer>> {

    public Integer getAttribute(S solution, int index) {
        return ((List<Integer>) solution.getAttribute(getAttributeIdentifier())).get(index);
    }

    public void setAttribute(S solution, int index, int value) {
        if (solution.getAttribute(getAttributeIdentifier()) == null) {
            solution.setAttribute(getAttributeIdentifier(), new ArrayList<>());
        }
        ((List<Integer>) solution.getAttribute(getAttributeIdentifier())).set(index, value);
    }
}
