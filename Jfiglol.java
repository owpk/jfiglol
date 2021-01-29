import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Jfiglol {
    private static final String VERBOSE_FORMAT = "%-15s: %s%n";
    private static final String[][][] colors256 = new String[6][][];
    private static Map<String, Request> globalOptions;
    private static Appender appender;
    private static Printer printer;

    static {
        fillColors();
    }

    /**
     * Interface to collect output data and pass it to printer
     */
    public interface Appender {
        List<String> getResult() throws IOException;
    }

    /**
     * Interface to print data
     */
    public interface Printer {
        void print(List<String> lines);
    }

    private static class AbsPrinter {
        protected boolean verboseRequested;
        protected boolean debugRequested;
        protected boolean animated;
    }

    private static class GradientPrinter implements Printer {
        protected Random random = new Random();
        protected int gradientLvl = 2;

        GradientPrinter(Request<Printer> request) {

        }

        @Override
        public void print(List<String> lines) {
            int colorBucket = random.nextInt(6);

            int count = 0;
            int yInd = 0;
            int xInd = 1;
            boolean xDirection = true;
            boolean yDirection = true;

            int gradient = 0;

            for (String var : lines) {

                if (xInd == colors256.length || xInd == 0) {
                    yInd = getByDirection(yDirection, yInd);
                    xDirection = !xDirection;
                    xInd = xDirection ? 2 : colors256.length - 2;
                }

                if (yInd == colors256.length || yInd == -1) {
                    yDirection = !yDirection;
                    colorBucket = (colorBucket + 1) % (colors256.length - 1);
                    yInd = yDirection ? 1 : 5;
                    xDirection = !xDirection;
                    xInd = xDirection ? 1 : colors256.length - 1;
                }

//                if (appRequests.get("-v").requested)
//                    System.out.printf("l %2s: x %s : y %s ", count++, xInd, yInd);

                System.out.printf("\033[38;5;%sm%s\033[0m%n",
                        colors256[colorBucket][yInd][xInd],
                        var);

                gradient++;
                if (gradient % gradientLvl == 0 && gradient != 0) {
                    xInd = getByDirection(xDirection, xInd);
                }
            }
        }

        private static int getByDirection(boolean direction, int input) {
            return direction ? ++input : --input;
        }
    }

    private static class RainbowPrinter implements Printer {
        protected float compression = 0.08f;
        protected float spread = 0.01f;
        protected float prec = 0.05f;

        public RainbowPrinter() {

        }

        public RainbowPrinter(Request<Printer> request) {
        }

        @Override
        public void print(List<String> lines) {
            String current;
            int ind = -1;
            StringBuilder sb = new StringBuilder();

            while (++ind < lines.size()) {
                current = lines.get(ind);

                char[] chars = current.toCharArray();
                for (int i = 0; i < chars.length; i++) {

                    float v = ind + i + (10 * prec) / spread;

                    float red = (float) (Math.sin(compression * v + 0) * 127 + 128);
                    float green = (float) (Math.sin(compression * v + 2 * (3.14 / 3)) * 127 + 128);
                    float blue = (float) (Math.sin(compression * v + 4 * (3.14 / 3)) * 127 + 128);

                    RGB rgb = new RGB(red, green, blue);

                    int xTermNumber = rgb.convertToXTermColor();

                    sb.append(String.format("\033[38;5;%dm%c\033[0m", xTermNumber, chars[i]));
                    // if (appRequests.get("-v").requested)
                    //     sb.append(rgb).append(" ").append("xTerm: ").append(xTermNumber).append(System.lineSeparator());
                }
                sb.append("\n");
            }
            System.out.print(sb.toString());
        }
    }

    private static class MonoPrinter implements Printer {
        private String color;

        MonoPrinter(Request<Printer> request) {

        }

        @Override
        public void print(List<String> lines) {
            int count = 0;
//            boolean verb = appRequests.get("-v").requested;

            for (String var : lines) {
//                if (verb)
//                    System.out.printf("l: %-2s ", count++);
                System.out.printf("\033[38;5;%sm%s\033[0m%n", color,
                        var);
            }
        }
    }

    private static class AnimatedMonoPrinter extends MonoPrinter {
        AnimatedMonoPrinter(Request<Printer> request) {
            super(request);
        }
    }

    private static class AnimatedGradientPrinter extends GradientPrinter {
        AnimatedGradientPrinter(Request<Printer> request) {
            super(request);
        }
    }

    private static class AnimatedRainbowPrinter extends RainbowPrinter {
        public AnimatedRainbowPrinter(Request<Printer> request) {
            super(request);
        }

        @Override
        public void print(List<String> lines) {
            float bias = 0f;
            double counter = 0;
            int ind = -1;
            String current;
            try {
                while (true) {
                    StringBuilder sb = new StringBuilder();
                    while (++ind < lines.size()) {
                        current = lines.get(ind);

                        char[] chars = current.toCharArray();
                        for (int i = 0; i < chars.length; i++) {

                            float v = ind + i + (10 * prec) / spread;

                            float red = (float) (Math.sin(bias + compression * v + 0) * 127 + 128);
                            float green = (float) (Math.sin(bias + compression * v + 2 * (3.14 / 3)) * 127 + 128);
                            float blue = (float) (Math.sin(bias + compression * v + 4 * (3.14 / 3)) * 127 + 128);

                            RGB rgb = new RGB(red, green, blue);
                            int xTermNumber = rgb.convertToXTermColor();

                            sb.append(String.format("\033[38;5;%dm%c\033[0m", xTermNumber, chars[i]));
//                        if (appRequests.get("-v").requested)
//                            sb.append(rgb).append(" ").append("xTerm: ").append(xTermNumber).append(System.lineSeparator());
                        }
                        sb.append("\n");
                    }
                    System.out.print(sb.toString() + "\n\n");
                    Thread.sleep(50);
                    for (int i = 0; i < lines.size() + 2; i++) {
                        System.out.printf("\033[%dA", 1);
                        System.out.print("\033[2K");

                    }
                    ind = -1;
                    prec += 0.01f;
                    bias = (float) Math.sin(counter);
                    counter += 0.1f;
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * User input requests container
     */
    private static class Request<T> {
        String[] names;
        boolean requested;
        List<String> args;
        String description;
        Class<?> clazz;

        Request(String description, Class<?> clazz, String... names) {
            this(description, names);
            this.clazz = clazz;
        }

        Request(String description, String... names) {
            this.names = names;
            this.description = description;
            args = new ArrayList<>();
        }

        void add(String arg) {
            args.add(arg);
        }

        void setRequested() {
            requested = true;
        }

        @Override
        public String toString() {
            return String.format(" --Request %s: args: %s", Arrays.toString(names), args);
        }
    }

    private static class FileAppender implements Appender {
        private final String file;

        public FileAppender(Request<Appender> request) {
            file = request.args.get(0);
        }

        @Override
        public List<String> getResult() throws IOException {
            return Files.readAllLines(Paths.get(file));
        }
    }

    public static class DefaultAppender implements Appender {
        private final String input;

        public DefaultAppender(Request<Appender> request) {
            this.input = request.args.get(0);
        }

        @Override
        public List<String> getResult() throws IOException {
            return new ArrayList<>(Arrays.asList(input.split("\\n")));
        }
    }

    /**
     * Contains utils for *.flf files
     */
    public static class FlfAppender implements Appender {
        private static final HashMap<Integer, Integer> OFFSET_MAP = new LinkedHashMap<>();
        private final String file;
        private final String input;
        private List<String> lines;
        private int height;
        private int heightD;
        private int lineLength;
        private int mode;
        private int comments;

        public FlfAppender(Request<Appender> request) {
            file = request.args.get(0);
            input = request.args.get(1);
        }

        /**
         * flf metadata pattern: [flf2a$ a b c d e
         * flf2 - "magic number" for file identification
         * a    - should always be `a', for now
         * $    - the "hardblank" -- prints as a blank, but can't be smushed
         * a    - height of a character
         * b    - height of a character, not including descenders
         * c    - max line length (excluding comment lines) + a fudge factor
         * d    - default smushmode for this font
         * e    - number of comment lines - note that first metaline not include in this number
         */
        protected void readMetaData(String meta) {
            String[] args = meta.split("\\s");
            try {
                height = Integer.parseInt(args[1]);
                heightD = Integer.parseInt(args[2]);
                lineLength = Integer.parseInt(args[3]);
                mode = Integer.parseInt(args[4]);
                comments = Integer.parseInt(args[5]);
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("Check font metadata in file \"%s\", Error msg %s", file, e.getLocalizedMessage());
            }
        }

        private void fillOffsets() {
            int lowerBound = '!' - 1;
            int upperBound = 'z' + 11;
            int metadataLine = 1;
            for (int i = 0; i < upperBound - lowerBound; i++) {
                OFFSET_MAP.put(i + lowerBound, comments + metadataLine + (i * height));
            }
        }

        @Override
        public List<String> getResult() throws IOException {
            lines = Files.readAllLines(Paths.get(file));
            readMetaData(lines.get(0));
            fillOffsets();
            String[] divided = input.split(" ");
            List<String> resultOutput = new ArrayList<>();
            for (String word : divided) {
                appendWord(resultOutput, word.toCharArray());
            }
            return resultOutput;
        }

        private void appendLine(StringBuilder sb, int seekPosition) {
            sb.append(lines.get(seekPosition));
        }

        private void appendWord(List<String> result, char[] parsed) {
            int i = 0;
            while (i < height) {
                StringBuilder sb = new StringBuilder();
                for (char letter : parsed) {
                    if (!OFFSET_MAP.containsKey((int) letter))
                        throw new IllegalArgumentException("Unexpected character: " + letter);
                    appendLine(sb, OFFSET_MAP.get((int) letter) + i);
                }
                result.add(sb.toString().replaceAll("@@", "").replaceAll("@", "").replaceAll("\\$", ""));
                i++;
            }
        }
    }

    /**
     * Convert RGB colors to palette terminal colors
     */
    private static class RGB {
        private static final int[] offset = {0, 95, 135, 175, 215, 255};
        private final int[] targets;

        public RGB(float red, float green, float blue) {
            targets = new int[]{Math.round(blue), Math.round(green), Math.round(red)};
        }

        public int convertToXTermColor() {
            int result = 16;
            int deep = 0;
            for (int i = targets.length - 1; i >= 0; i--) {
                if (targets[i] >= offset[1] && i != 0) {
                    int multiplier = deep(i, offset.length);
                    int localResult = multiplier * findOffset(targets[i]);
                    deep += localResult;
                } else if (targets[i] >= offset[i] && i == 0) {
                    deep += findOffset(targets[i]);
                }
            }
            return deep + result;
        }

        private int findOffset(int target) {
            int k = 20;
            for (int j = 0; j < offset.length; j++) {
                if (j < offset.length - 1)
                    k = (offset[j + 1] - offset[j]) / 2;
                if (target - k < offset[j]) return j;
            }
            return 0;
        }

        private int deep(int ind, int deep) {
            if (ind <= 1) return deep;
            return deep(--ind, deep * 6);
        }

        public String toString() {
            return String.format("R:%d, G:%d, B:%d ", targets[2], targets[1], targets[0]);
        }
    }

    private static abstract class ArgumentHandler<T> {
        protected ArgumentHandler<?> next;
        protected Request<T> request;
        protected Map<String, Request<T>> localRequests;

        protected abstract Map<String, Request<T>> getLocalRequests();

        public ArgumentHandler() {
            localRequests = getLocalRequests();
        }

        void handle(String[] args) {
            boolean requestFound = false;
            int off = 0;
            cx:
            for (String arg : args) {
                for (Map.Entry<String, Request<T>> entry : localRequests.entrySet()) {
                    if (arg.startsWith("-") && requestFound) {
                        if (this.next != null) {
                            String[] arr = new String[args.length - off - 1];
                            System.arraycopy(args, off + 1, arr, 0, arr.length);
                            this.next.handle(arr);
                        }
                        return;
                    } else if (requestFound) {
                        off++;
                        request.add(arg);
                    }

                    if (!requestFound)
                        requestFound = resolve(entry.getValue(), arg);

                    if (requestFound)
                        continue cx;

                }
                if (!requestFound) {
                    handleUnrecognizedOption(arg);
                }
            }
        }

        protected void handleUnrecognizedOption(String arg) {
            System.out.println("Unrecognized option: " + arg);
            throwError();
        }

        protected void throwError() {

        }

        protected boolean resolve(Request<T> request, String arg) {
            for (String name : localRequests.get(request.names[0]).names) {
                if (arg.equals(name)) {
                    this.request = request;
                    request.setRequested();
                    return true;
                }
            }
            return false;
        }

        void setNext(ArgumentHandler<?> next) {
            this.next = next;
        }
    }

    private static class ModeHandler extends ArgumentHandler<Appender> {
        @Override
        protected void throwError() {
            System.exit(0);
        }

        protected Map<String, Request<Appender>> getLocalRequests() {
            return new LinkedHashMap<>(Map.of(
                    "--plain", new Request<>("plain text", DefaultAppender.class, "--plain"),
                    "--font", new Request<>("font", FlfAppender.class, "--font"),
                    "--file", new Request<>("file", FileAppender.class, "--file")
            ));
        }

        public Appender getAppender() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return (Appender) this.request.clazz.getConstructor(Request.class).newInstance(this.request);
        }
    }

    private static class PrinterHandler extends ArgumentHandler<Printer> {
        @Override
        protected void throwError() {
            System.exit(0);
        }

        protected Map<String, Request<Printer>> getLocalRequests() {
            boolean animated = globalOptions.containsKey("--animated");
            return new LinkedHashMap<>(Map.of(
                    "-m", new Request<>("mono color", animated ?
                            AnimatedMonoPrinter.class : MonoPrinter.class,
                            "-m", "--mono"),

                    "-g", new Request<>("plain text", animated ?
                            AnimatedGradientPrinter.class : GradientPrinter.class,
                            "-g", "--gradient"),

                    "-r", new Request<>("rainbow", animated ?
                            AnimatedRainbowPrinter.class : RainbowPrinter.class,
                            "-r", "--rainbow")
            ));
        }

        public Printer getPrinter() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return (Printer) this.request.clazz.getConstructor(Request.class).newInstance(this.request);
        }
    }

    private static class ExtendsHandler extends ArgumentHandler {

        protected Map<String, Request> getLocalRequests() {
            return new LinkedHashMap<>(Map.of(
                    "-a", new Request("mono color", "-a", "--animated"),
                    "-r", new Request("plain text", "-r", "--random"),
                    "-d", new Request("rainbow", "-d", "--debug"),
                    "-v", new Request("rainbow", "-v", "--verbose")
            ));
        }
    }
    
    public static void main(String... args) throws Exception {
        if (args[0].equals("--help")) {
            help();
            return;
        } else if (args[0].equals("--palette")) {
            palette();
            return;
        }

        ExtendsHandler extendsHandler = new ExtendsHandler();
        ModeHandler modeHandler = new ModeHandler();
        PrinterHandler printerHandler = new PrinterHandler();

//        extendsHandler.setNext(modeHandler);
        modeHandler.setNext(printerHandler);
        extendsHandler.handle(args);

//        globalOptions = extendsHandler.getGlobalOptions();
        appender = modeHandler.getAppender();
        printer = printerHandler.getPrinter();

        printer.print(appender.getResult());
    }

    private static void fillColors() {
        int ind = 16;
        for (int i = 0; i < colors256.length; i++) {
            colors256[i] = new String[6][6];
            for (int y = 0; y < colors256[i].length; y++) {
                for (int x = 0; x < colors256[i].length; x++) {
                    colors256[i][y][x] = String.format("%03d", ind++);
                }
            }
        }
    }
    
    public static void palette() {
        StringBuilder sb = new StringBuilder();
        for (String[][] strings : colors256) {
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
        String help = "Usage: java Jfiglol [mode] [printer] [options]\n" +
                "mode :    [--plain {\"User input\"} |\n" +
                "           --font {path/to/font_file.flf, \"User input\"} |\n" +
                "           --file {path}] \n\n" +
                "printer : [--mono {color (range 16...255)} |\n" +
                "           --gradient {level (float number, default 0.3)} |\n" +
                "           --rainbow {0, 0} ]\n\n" +
                "options:  [--animated {speed} |\n" +
                "           --random |\n" +
                "           --debug |\n" +
                "           --verbose]\n\n" +
                "Examples: \n" +
                "         java Jfiglol --font \"./fonts/3d.flf\" \"Hello World!\" --rainbow 0.3 --animated\n" +
                "         java Jfiglol --plain \"Hello World!\" --gradient 0.2\n" +
                "         java Jfiglol --file \"./examples/example.txt\" --mono 144\n" +
                "\n\n" +
                "Additions: Jfiglol [--help | --palette-table]\n" +
                "Examples:\n" +
                "         java Jfiglol --help\n" +
                "         java Jfiglol --palette\n" +
                "\n" +
                "With passing arguments via pipeline:\n" +
                "         echo \"Hello World!\" | xargs -I {} java Jfiglol --plain \"{}\" --rainbow --animated";
        new RainbowPrinter().print(new ArrayList<>(Arrays.asList(help.split("\\n"))));
    }
}


