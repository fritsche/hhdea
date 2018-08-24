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
package br.ufpr.inf.cbio.hhdea.config;

import br.ufpr.inf.cbio.hhdea.algorithm.HHdEA.HHdEAConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.HypE.HypEConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEAD.MOEADConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEADD.MOEADDConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOMBI2.MOMBI2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAII.NSGAIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAIII.NSGAIIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2.SPEA2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE.SPEA2SDEConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA.ThetaDEAConfiguration;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class AlgorithmConfigurationFactory {

    public static AlgorithmConfiguration getAlgorithmConfiguration(String algorithm) {
        switch (algorithm) {
            case "ThetaDEA":
                return new ThetaDEAConfiguration();
            case "NSGAIII":
                return new NSGAIIIConfiguration();
            case "MOEADD":
                return new MOEADDConfiguration();
            case "MOEAD":
                return new MOEADConfiguration();
            case "NSGAII":
                return new NSGAIIConfiguration();
            case "SPEA2":
                return new SPEA2Configuration();
            case "SPEA2SDE":
                return new SPEA2SDEConfiguration();
            case "HypE":
                return new HypEConfiguration();
            case "MOMBI2":
                return new MOMBI2Configuration();
            default:
                return new HHdEAConfiguration(algorithm);
        }
    }

}
