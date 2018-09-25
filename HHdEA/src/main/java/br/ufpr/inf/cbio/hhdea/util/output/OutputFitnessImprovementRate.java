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
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.util.JMetalException;

/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class OutputFitnessImprovementRate implements Observer {

    BufferedWriter writer;

    public OutputFitnessImprovementRate(String experimentBaseDirectory,
            String methodologyName, String m, String algorithmName, String problemName, String id) {

        String folder = experimentBaseDirectory + "/"
                + methodologyName + "/"
                + m
                + "/output/"
                + algorithmName + "/"
                + problemName + "/";
        Utils outputUtils = new Utils(folder);
        outputUtils.prepareOutputDirectory();
        writer = getFileWriter(folder + "/fir." + id);
    }

    /**
     * Print current FIR to buffer. Obserbable must extends HyperHeuristic. The
     * file buffer is closed when the stopping condition is reached.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof HyperHeuristic) {
            try {
                HyperHeuristic hh = (HyperHeuristic) o;
                double fir = hh.getFir();
                writer.write(Double.toString(fir));
                writer.newLine();
                if (hh.isStoppingConditionReached()) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(OutputFitnessImprovementRate.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private BufferedWriter getFileWriter(String fileName) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new JMetalException("Exception when calling method getFileWriter()", e);
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        return new BufferedWriter(outputStreamWriter);
    }
}
