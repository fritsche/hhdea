package br.ufpr.inf.cbio.hhdea.runner;

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

    public static CommandLine parse(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        options.addOption(Option.builder("h").longOpt("help").desc("print this message").build());

        options.addOption(Option.builder("P").longOpt("output-path").hasArg().argName("path")
                .desc("directory path for output (if no path is given experiment/ will be used.)").build());

        options.addOption(Option.builder("M").longOpt("methodology").hasArg().argName("methodology")
                .desc("set the methodology to be used: NSGAIII (default), MaF, Arion.").build());

        options.addOption(Option.builder("a").longOpt("algorithm").hasArg().argName("algorithm").required()
                .desc("set the algorithm to be executed: NSGAII, MOEAD, MOEADD, ThetaDEA, NSGAIII, SPEA2, SPEA2SDE, HypE, MOMBI2, Traditional, <other>."
                        + "If <other> name is given, HHdEA will be executed and the algorithm output name will be <other>.").build());

        options.addOption(Option.builder("p").longOpt("problem").hasArg().argName("problem").required()
                .desc("set the problem instance: DTLZ[1-7], WFG[1-9], MinusDTLZ[1-7], MinusWFG[1-9], MaF[1-15]; "
                        + "--methodology must be set accordingly.").build());

        options.addOption(Option.builder("m").longOpt("objectives").hasArg().argName("objectives").required()
                .desc("set the number of objectives to <objectives>, --problem and --methodology must be set acordingly.").build());

        options.addOption(Option.builder("id").hasArg().argName("id")
                .desc("independent run id, default 0.").build());

        options.addOption(Option.builder("s").longOpt("seed").hasArg().argName("seed")
                .desc("set the seed for JMetalRandom, default System.currentTimeMillis()").build());

        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("h")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("hhdea", options);
        }
        return cmd;
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
            runner.setMethodologyName(methodologyName);
        }
        if ((seedStr = cmd.getOptionValue("s")) != null) {
            runner.setSeed(Long.parseLong(seedStr));
        }
        return runner;
    }

    public static void main(String[] args) {
        JMetalLogger.logger.setLevel(Level.ALL);
        try {
            Runner runner = getRunner(parse(args));
            runner.run();
            runner.printResult();
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
