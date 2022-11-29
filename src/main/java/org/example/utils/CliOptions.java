package org.example.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CliOptions {
    public static final Option PORT = new Option("p", "port", true, "Port");
    public static final Option THREADS = new Option("t", "threads", true, "Thread count");
    public static final Option PRINT_DIRECTORY = new Option("d", "Print directory content");
    public static final Option HELP = new Option("h", "help", false, "Show commands descriptions.");


    public static Options getOptions() {
        return new Options()
                .addOption(PORT)
                .addOption(THREADS)
                .addOption(PRINT_DIRECTORY);
    }
}
