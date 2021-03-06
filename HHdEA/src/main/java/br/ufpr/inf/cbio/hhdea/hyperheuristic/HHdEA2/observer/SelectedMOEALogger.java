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

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class SelectedMOEALogger extends HHdEA2Logger {

    public SelectedMOEALogger(String folder, String file) {
        super(folder, file);
    }

    @Override
    public void update(HHdEA2 hhdea2) {
        CooperativeAlgorithm selected = hhdea2.getSelected();
        List<CooperativeAlgorithm> algorithms = hhdea2.getAlgorithms();
        ow.writeLine(Integer.toString(algorithms.indexOf(selected)));
    }
}
