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

import br.ufpr.inf.cbio.hhdea.algorithm.HypE.HypE;
import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.HHdEA.HHdEAConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.HypE.HypEConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEAD.MOEADConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEADD.MOEADD;
import br.ufpr.inf.cbio.hhdea.algorithm.MOEADD.MOEADDConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.MOMBI2.MOMBI2;
import br.ufpr.inf.cbio.hhdea.algorithm.MOMBI2.MOMBI2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAII.NSGAIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAIII.NSGAIII;
import br.ufpr.inf.cbio.hhdea.algorithm.NSGAIII.NSGAIIIConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2.SPEA2Configuration;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE.SPEA2SDE;
import br.ufpr.inf.cbio.hhdea.algorithm.SPEA2SDE.SPEA2SDEConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA.ThetaDEA;
import br.ufpr.inf.cbio.hhdea.algorithm.ThetaDEA.ThetaDEAConfiguration;
import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.traditional.Traditional;
import br.ufpr.inf.cbio.hhdea.algorithm.hyperheuristic.traditional.TraditionalConfiguration;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class AlgorithmConfigurationFactory {

    public static AlgorithmConfiguration getAlgorithmConfiguration(String algorithm) {
        if (algorithm.equals(ThetaDEA.class.getSimpleName())) {
            return new ThetaDEAConfiguration();
        } else if (algorithm.equals(NSGAIII.class.getSimpleName())) {
            return new NSGAIIIConfiguration();
        } else if (algorithm.equals(MOEADD.class.getSimpleName())) {
            return new MOEADDConfiguration();
        } else if (algorithm.equals(MOEAD.class.getSimpleName())) {
            return new MOEADConfiguration();
        } else if (algorithm.equals(NSGAII.class.getSimpleName())) {
            return new NSGAIIConfiguration();
        } else if (algorithm.equals(SPEA2.class.getSimpleName())) {
            return new SPEA2Configuration();
        } else if (algorithm.equals(SPEA2SDE.class.getSimpleName())) {
            return new SPEA2SDEConfiguration();
        } else if (algorithm.equals(HypE.class.getSimpleName())) {
            return new HypEConfiguration();
        } else if (algorithm.equals(MOMBI2.class.getSimpleName())) {
            return new MOMBI2Configuration();
        } else if (algorithm.equals(Traditional.class.getSimpleName())) {
            return new TraditionalConfiguration();
        } else {
            return new HHdEAConfiguration(algorithm);
        }
    }
}
