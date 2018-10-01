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
import java.util.logging.Level;
import org.uma.jmetal.util.JMetalLogger;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class OutputCountOfApplication implements Observer {

    private final OutputWriter ow;

    public OutputCountOfApplication(String folder, String file) {
        JMetalLogger.logger.log(Level.CONFIG, "Output Fitness Improvement Rate: ENABLED");
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
            int[] count = hh.getCount();
            String line = "";
            for (Integer i : count) {
                line += i + "\t";
            }
            ow.writeLine(line);
        }
    }

    public void close() {
        ow.close();
    }

}
