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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.selection;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <T>
 */
public interface SelectionFunction<T> {

    public void init();

    public void add(T t);

    public T getNext();

    /**
     * Assign credit to the last given low-level heuristic positive fitness
     * value means improvement negative fitness value means worsens zero fitness
     * value means no improvement or worsens
     * @param reward
     */
    public void creditAssignment(double reward);
}
