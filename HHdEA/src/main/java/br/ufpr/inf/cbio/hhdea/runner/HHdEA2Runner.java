/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche at gmail.com>
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
package br.ufpr.inf.cbio.hhdea.runner;

import br.ufpr.inf.cbio.hhdea.problem.ProblemFactory;
import static br.ufpr.inf.cbio.hhdea.runner.Main.help;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalLogger;

/**
 *
 * @author Gian Fritsche <gian.fritsche at gmail.com>
 */
public class HHdEA2Runner {

    private Problem problem;

    public static void main(String[] args) {
        try {
            JMetalLogger.logger.setLevel(Level.ALL);

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd;
            Options options = new Options();

            options.addOption(Option.builder("h").longOpt("help").desc("print this message and exits.").build());
            options.addOption(Option.builder("id").hasArg().argName("id")
                    .desc("set the independent run id, default 0.").build());
            options.addOption(Option.builder("s").longOpt("seed").hasArg().argName("seed")
                    .desc("set the seed for JMetalRandom, default System.currentTimeMillis()").build());
            options.addOption(Option.builder("p").longOpt("problem").hasArg().argName("problem")
                    .desc("set the problem instance: MaF[1-15]").build());
            options.addOption(Option.builder("m").longOpt("objectives").hasArg().argName("objectives")
                    .desc("set the number of objectives to <objectives> (default value is 3). <problem> and <methodology> must be set acordingly.").build());

            // parse command line
            cmd = parser.parse(options, args);
            // print help and exit
            if (cmd.hasOption("h") || args.length == 0) {
                help(options);
                System.exit(0);
            }
            String aux;
            if ((aux = cmd.getOptionValue("p")) != null) {
                problem = ProblemFactory.getProblem(problemName, m);
            }
            JMetalLogger.logger.log(Level.CONFIG, "Problem: {0} with {1} objectives", new Object[]{problemName, m});
        } catch (ParseException ex) {
            Logger.getLogger(HHdEA2Runner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
