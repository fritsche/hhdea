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
package br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA2.observer;

import br.ufpr.inf.cbio.hhdea.hyperheuristic.CooperativeAlgorithm;
import br.ufpr.inf.cbio.hhdea.hyperheuristic.HHdEA2.HHdEA2;
import br.ufpr.inf.cbio.hhdea.util.output.OutputWriter;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import org.uma.jmetal.util.JMetalLogger;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class MOEASFIRLogger implements HHdEA2Logger {

    private final OutputWriter ow;

    public MOEASFIRLogger(String folder, String file) {
        JMetalLogger.logger.log(Level.CONFIG, "Fitness Improvement Rate Logger: ENABLED");
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
        HHdEA2 hhdea2 = (HHdEA2) o;

        List<CooperativeAlgorithm> algorithms = hhdea2.getAlgorithms();
        Map<CooperativeAlgorithm, Double> map = hhdea2.getMoeasfir();

        StringBuilder buffer = new StringBuilder();
        for (CooperativeAlgorithm algorithm : algorithms) {
            buffer.append(map.get(algorithm));
            buffer.append("\t");
        }
        ow.writeLine(buffer.toString());
    }

    /**
     * Close buffer and write to file
     */
    @Override
    public void close() {
        ow.close();
    }

}
