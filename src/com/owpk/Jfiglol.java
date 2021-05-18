package com.owpk;

import com.owpk.appender.Appender;
import com.owpk.handler.ExtendsHandler;
import com.owpk.handler.HandlerContext;
import com.owpk.handler.ModeHandler;
import com.owpk.handler.PrinterHandler;
import com.owpk.printer.Printer;
import com.owpk.printer.RainbowPrinter;

import java.util.*;
import java.util.stream.Collectors;

public class Jfiglol {
    public static final Random RANDOM = new Random();
    public static final String VERBOSE_FORMAT = "%-15s: %s%n";
    public static final String[][][] COLORS_256 = new String[6][][];
    public static final Map<String, Request> GLOBAL_OPTIONS = new LinkedHashMap<>();
    private static Queue<String> args;

    static {
        fillColors();
    }

    public static void main(String... args) throws Exception {
        if (args[0].equals("--help")) {
            help();
            return;
        } else if (args[0].equals("--palette")) {
            palette();
            return;
        }

        Jfiglol.args = Arrays.stream(args).collect(Collectors.toCollection(ArrayDeque::new));

        HandlerContext ctx = new HandlerContext(Jfiglol.args);

        Appender appender = ctx.getAppender();
        Printer printer = ctx.getPrinter();

        if (GLOBAL_OPTIONS.containsKey("-v")) {
            System.out.print(appender);
            System.out.print(printer);
        }
        printer.print(appender.getResult());
    }

    private static void fillColors() {
        int ind = 16;
        for (int i = 0; i < COLORS_256.length; i++) {
            COLORS_256[i] = new String[6][6];
            for (int y = 0; y < COLORS_256[i].length; y++) {
                for (int x = 0; x < COLORS_256[i].length; x++) {
                    COLORS_256[i][y][x] = String.format("%03d", ind++);
                }
            }
        }
    }

    public static void palette() {
        StringBuilder sb = new StringBuilder();
        for (String[][] strings : COLORS_256) {
            for (String[] string : strings) {
                for (String s : string) {
                    sb.append("\033[38;5;")
                            .append(s).append("m")
                            .append(s)
                            .append("\033[0m")
                            .append(" ");
                }
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
    }

    public static void help() {
        String mode = collect(ModeHandler.LOCAL);
        String printer = collect(PrinterHandler.LOCAL);
        String options = collect(ExtendsHandler.LOCAL);
        String format =
                "Fast start: jfiglol \"Your input here\" (prints rainbow text with 3d font by default)" +
                        "Usage with other args: jfiglol [mode] [printer] [options]\n" +
                        "mode:\n" + mode +
                        "printer:\n" + printer +
                        "options:\n" + options +
                        "Examples: \n" +
                        "         jfiglol -f \"./fonts/3d.flf\" \"Hello World!\" --rainbow 0.3 --animated\n" +
                        "         jfiglol -p \"Hello World!\" --gradient 0.2\n" +
                        "         jfiglol -F \"./examples/example.txt\" --mono 144\n" +
                        "\n\n" +
                        "Additions: jfiglol [ --help | --palette ]\n" +
                        "Examples:\n" +
                        "         jfiglol --help\n" +
                        "         jfiglol --palette\n" +
                        "\n" +
                        "With passing arguments via pipeline:\n" +
                        "         echo \"Hello World!\" | xargs -I {} jfiglol --plain \"{}\" --rainbow --animated";
        new RainbowPrinter().print(new ArrayList<>(Arrays.asList(format.split("\\n"))));
    }

    private static String collect(Map<String, Request> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> {
            sb.append("         ");
            for (String arg : v.getNames()) {
                sb.append(arg).append(" ");
            }
            sb.append(v.getHelp());
            sb.append("\n");
        });
        return sb.toString();
    }

    public static Queue<String> getArgs() {
        return args;
    }

    public static void setArgs(Queue<String> args) {
        Jfiglol.args = args;
    }
}
