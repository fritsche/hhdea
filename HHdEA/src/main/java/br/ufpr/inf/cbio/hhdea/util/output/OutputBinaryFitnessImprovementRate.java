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
package br.ufpr.inf.cbio.hhdea.util.output;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.HyperHeuristic;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class OutputBinaryFitnessImprovementRate implements Observer {

    private final OutputWriter ow;

    public OutputBinaryFitnessImprovementRate(String folder, String file) {
        ow = new OutputWriter(folder, file);
    }

    /**
     * Print current FIR to buffer. Observable must extends HyperHeuristic.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof HyperHeuristic) {
            HyperHeuristic hh = (HyperHeuristic) o;
            double fir = hh.getFir();
            ow.writeLine(Integer.toString(fir >= .0 ? 1 : 0));
        }
    }

    public void close() {
        ow.close();
    }

}
