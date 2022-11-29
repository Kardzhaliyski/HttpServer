package org.example;

import org.apache.commons.cli.*;
import org.example.utils.CliOptions;

import java.io.IOException;

public class Main {
    public static HelpFormatter helpFormatter = new HelpFormatter();

    public static void main(String[] args) {
        CommandLine cli;
        try {
            cli = new DefaultParser().parse(CliOptions.getOptions(), args);
        } catch (ParseException e) {
            System.out.println("Invalid Input!");
            return;
        }

        if (cli.getOptionValue(CliOptions.HELP) != null) {
            printUsage();
            return;
        }

        ServerBuilder builder = new ServerBuilder(cli);
        Server server = builder.build();

        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e); //todo
        }
    }

    public static void printUsage() {
        helpFormatter.printHelp("http-server [PATH] [OPTIONS]", CliOptions.getOptions());
    }
}