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
package br.ufpr.inf.cbio.hhdea.util;

import org.uma.jmetal.util.JMetalException;

import java.io.File;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class OutputUtils {

    private String experimentBaseDirectory;

    public OutputUtils(String experimentBaseDirectory) {
        this.experimentBaseDirectory = experimentBaseDirectory;
    }

    public void prepareOutputDirectory() {
        if (experimentDirectoryDoesNotExist()) {
            createExperimentDirectory();
        }
    }

    private boolean experimentDirectoryDoesNotExist() {
        boolean result;
        File experimentDirectory;

        experimentDirectory = new File(experimentBaseDirectory);
        if (experimentDirectory.exists() && experimentDirectory.isDirectory()) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    private void createExperimentDirectory() {
        File experimentDirectory;
        experimentDirectory = new File(experimentBaseDirectory);

        if (experimentDirectory.exists()) {
            experimentDirectory.delete();
        }

        boolean result;
        result = new File(experimentBaseDirectory).mkdirs();
        if (!result) {
            throw new JMetalException("Error creating experiment directory: "
                    + experimentBaseDirectory);
        }
    }
}
