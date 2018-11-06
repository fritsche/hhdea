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
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class MOEASFIRLogger extends HHdEA2Logger {

    public MOEASFIRLogger(String folder, String file) {
        super(folder, file);
    }

    @Override
    public void update(HHdEA2 hhdea2) {
        List<CooperativeAlgorithm> algorithms = hhdea2.getAlgorithms();
        Map<CooperativeAlgorithm, Double> map = hhdea2.getMoeasfir();
        StringBuilder buffer = new StringBuilder();
        for (CooperativeAlgorithm algorithm : algorithms) {
            buffer.append(map.get(algorithm));
            buffer.append("\t");
        }
        ow.writeLine(buffer.toString());
    }
}
