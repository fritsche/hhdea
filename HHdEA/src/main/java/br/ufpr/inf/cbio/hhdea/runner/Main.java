package br.ufpr.inf.cbio.hhdea.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

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
/**
 *
 * @author Gian Fritsche <gmfritsche at inf.ufpr.br>
 */
public class Main {

    public static CommandLine parse(String[] args) {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        Options options = new Options();

        /**
         * When args is empty or -h is used do not throw exception for required
         * arguments. To achieve this we need to parse command line twice (with
         * and without considering required arguments).
         * https://stackoverflow.com/questions/36720946/apache-cli-required-options-contradicts-with-help-option
         */
        try {
            // optional arguments
            List<Option> optional = new ArrayList<>();
            optional.add(Option.builder("h").longOpt("help").desc("print this message and exits.").build());
            optional.add(Option.builder("P").longOpt("output-path").hasArg().argName("path")
                    .desc("directory path for output (if no path is given experiment/ will be used.)").build());
            optional.add(Option.builder("M").longOpt("methodology").hasArg().argName("methodology")
                    .desc("set the methodology to be used: NSGAIII (default), MaF, Arion.").build());
            optional.add(Option.builder("id").hasArg().argName("id")
                    .desc("set the independent run id, default 0.").build());
            optional.add(Option.builder("s").longOpt("seed").hasArg().argName("seed")
                    .desc("set the seed for JMetalRandom, default System.currentTimeMillis()").build());
            // add optional arguments
            for (Option option : optional) {
                options.addOption(option);
            }
            // required arguments
            List<Option> required = new ArrayList<>();
            required.add(Option.builder("a").longOpt("algorithm").hasArg().argName("algorithm")
                    .desc("set the algorithm to be executed: NSGAII, MOEAD, MOEADD, ThetaDEA, NSGAIII, SPEA2, SPEA2SDE, HypE, MOMBI2, Traditional, <other>."
                            + "If <other> name is given, HHdEA will be executed and the algorithm output name will be <other>.").build());
            required.add(Option.builder("p").longOpt("problem").hasArg().argName("problem")
                    .desc("set the problem instance: DTLZ[1-7], WFG[1-9], MinusDTLZ[1-7], MinusWFG[1-9], MaF[1-15]; "
                            + "<methodology> must be set accordingly.").build());
            required.add(Option.builder("m").longOpt("objectives").hasArg().argName("objectives")
                    .desc("set the number of objectives to <objectives>. <problem> and <methodology> must be set acordingly.").build());
            // add required argumengs (without required flag)
            for (Option option : required) {
                options.addOption(option);
            }
            // parse command line without considering required arguments
            cmd = parser.parse(options, args);
            // clear options
            options = new Options();
            // add optional arguments
            for (Option option : optional) {
                options.addOption(option);
            }
            // add required arguments (with the required flag)
            for (Option option : required) {
                option.setRequired(true);
                options.addOption(option);
            }
            // print help and exit
            if (cmd.hasOption("h") || args.length == 0) {
                help(options);
                System.exit(0);
            }
            // parse command line considering required arguments
            cmd = parser.parse(options, args);
            return cmd;
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                    "Failed to parse command line arguments. Execute with -h for usage help.", ex);
        }
        return null;
    }

    public static void help(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "java -cp <jar> br.ufpr.inf.cbio.hhdea.runner.Main",
                "\nExecute a single independent run of the <algorithm> on a given <problem>.\n",
                options,
                "\nPlease report issues at https://github.com/fritsche/hhdea/issues", true);
    }

    public static Runner getRunner(CommandLine cmd) {
        String algorithmName, problemName, objectives, idStr, seedStr, experimentBaseDirectory, methodologyName;

        // required arguments
        if ((algorithmName = cmd.getOptionValue("a")) == null) {
            throw new JMetalException("Parameter -a is required");
        }
        if ((problemName = cmd.getOptionValue("p")) == null) {
            throw new JMetalException("Parameter -p is required");
        }
        if ((objectives = cmd.getOptionValue("m")) == null) {
            throw new JMetalException("Parameter -m is required");
        }
        Runner runner = new Runner(algorithmName, problemName, Integer.parseInt(objectives));

        // optional arguments
        if ((experimentBaseDirectory = cmd.getOptionValue("P")) != null) {
            runner.setExperimentBaseDirectory(experimentBaseDirectory);
        }
        if ((idStr = cmd.getOptionValue("id")) != null) {
            runner.setId(Integer.parseInt(idStr));
        }
        if ((methodologyName = cmd.getOptionValue("M")) != null) {
            runner.setMethodologyName(methodologyName + "Methodology");
        }
        if ((seedStr = cmd.getOptionValue("s")) != null) {
            runner.setSeed(Long.parseLong(seedStr));
        }
        return runner;
    }

    public static void main(String[] args) {
        JMetalLogger.logger.setLevel(Level.ALL);
        Runner runner = getRunner(parse(args));
        runner.run();
        runner.printResult();
    }
}
