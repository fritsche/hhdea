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
package br.ufpr.inf.cbio.hhdea.algorithm.HHdEA;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 * @version 0.1 
 * - Every MOEA uses a complete population
 * - Decomposition based MOEAs uses all Weight Vectors
 */
public class HHdEA<S extends Solution<?>> implements Algorithm<List<S>> {

    protected int maxEvaluations;
    protected Problem<S> problem;

    protected List<CooperativeAlgorithm<S>> algorithms;

    @Override
    public void run() {
        
        int evaluations = 0;
        
        

    }

    @Override
    public List<S> getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "HHdEA";
    }

    @Override
    public String getDescription() {
        return "Hyper-heuristics for distributed Evolutionary Algorithms";
    }

}
