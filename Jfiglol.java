import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Jfiglol {
    private static final Random RANDOM = new Random();
    private static final String VERBOSE_FORMAT = "%-15s: %s%n";
    private static final String[][][] COLORS_256 = new String[6][][];
    private static final Map<String, Request> GLOBAL_OPTIONS = new LinkedHashMap<>();

    static {
        fillColors();
    }

    /**
     * Interface to collect output data and pass it to printer
     */
    public interface Appender {
        default String print() {
            return String.format(VERBOSE_FORMAT, "mode", getName());
        }

        List<String> getResult() throws IOException;

        String getName();
    }

    /**
     * Interface to print data
     */
    public interface Printer {
        void print(List<String> lines);
    }

    /**
     * Abstract printer, base output printer class contains method
     * to print and/or animate output
     */
    public static abstract class AbsPrinter implements Printer {
        protected boolean debugRequested;
        protected boolean randomRequested;
        protected boolean animated;
        protected int animationSpeed;

        public AbsPrinter() {
            debugRequested = GLOBAL_OPTIONS.containsKey("-d");
            animated = GLOBAL_OPTIONS.containsKey("-a");
            randomRequested = GLOBAL_OPTIONS.containsKey("-r");
        }

        @Override
        public void print(List<String> lines) {
            if (animated) animatedPrint(lines);
            else simplePrint(lines);
        }

        protected void animatedPrint(List<String> lines) {
            try {
                while (true) {
                    StringBuilder sb = new StringBuilder();
                    build(lines, sb);
                    animate();
                    System.out.print(sb.toString() + "\n\n");
                    Thread.sleep(animationSpeed == 0 ? 100 : animationSpeed);
                    for (int i = 0; i < lines.size() + 2; i++) {
                        System.out.printf("\033[%dA", 1);
                        System.out.print("\033[2K");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        protected void simplePrint(List<String> lines) {
            StringBuilder sb = new StringBuilder();
            build(lines, sb);
            System.out.print(sb.toString());
        }

        protected int getByDirection(boolean direction, int input) {
            return direction ? ++input : --input;
        }

        protected abstract void build(List<String> lines, StringBuilder sb);

        protected abstract void animate();

        protected abstract String getName();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(VERBOSE_FORMAT, "printer name", getName()));
            sb.append(String.format(VERBOSE_FORMAT, "debug", debugRequested));
            sb.append(String.format(VERBOSE_FORMAT, "random values", randomRequested));
            sb.append(String.format(VERBOSE_FORMAT, "animation", animated));
            if (animated)
                sb.append(String.format(VERBOSE_FORMAT, "animation speed", animationSpeed));
            return sb.toString();
        }
    }

    /**
     * User input requests
     */
    private static class Request {
        private String[] names;
        private List<String> args;
        private String help;
        private Class<?> clazz;

        Request(String help, Class<?> clazz, String... names) {
            this(help, names);
            this.clazz = clazz;
        }

        Request(String help, String... names) {
            this.names = names;
            this.help = help;
            args = new ArrayList<>();
        }

        void add(String arg) {
            args.add(arg);
        }

        @Override
        public String toString() {
            return String.format(" --Request %s: args: %s", Arrays.toString(names), args);
        }
    }

    public static class GradientPrinter extends AbsPrinter {
        public static int DEFAULT_GRADIENT = 2;
        protected int colorBucket;
        protected int yInd;
        protected int xInd;
        protected boolean xDirection;
        protected boolean yDirection;
        protected int gradientLvl;

        public GradientPrinter(Request request) {
            xInd = 1;
            xDirection = true;
            yDirection = true;
            gradientLvl = request.args.size() > 0 ? Integer.parseInt(request.args.get(0)) :
                    randomRequested ? RANDOM.nextInt(5) + 1 : DEFAULT_GRADIENT;
            colorBucket = randomRequested ? RANDOM.nextInt(COLORS_256.length) : 0;
            animationSpeed = 100;
        }

        @Override
        protected void build(List<String> lines, StringBuilder sb) {
            int ind = -1;
            String current;
            int gradient = 0;
            while (++ind < lines.size()) {
                current = lines.get(ind);
                if (xInd >= COLORS_256.length || xInd <= 0) {
                    yInd = getByDirection(yDirection, yInd);
                    xDirection = !xDirection;
                    xInd = xDirection ? 1 : COLORS_256.length - 1;
                }

                if (yInd >= COLORS_256.length || yInd < 0) {
                    yDirection = !yDirection;
                    colorBucket = (colorBucket + 1) % COLORS_256.length;
                    yInd = yDirection ? 0 : 5;
                    xDirection = !xDirection;
                    xInd = xDirection ? 1 : COLORS_256.length - 1;
                }

                if (debugRequested)
                    sb.append(String.format("b %d : x %2s: y %2s c: %s ", colorBucket, xInd, yInd, COLORS_256[colorBucket][yInd][xInd]));

                sb.append(String.format("\033[38;5;%sm%s\033[0m%n",
                        COLORS_256[colorBucket][yInd][xInd],
                        current));
                gradient++;
                if (gradient % gradientLvl == 0 && gradient != 0) {
                    xInd = getByDirection(xDirection, xInd);
                }
            }
        }

        @Override
        protected void animate() {
            xInd = getByDirection(xDirection, xInd);
        }

        @Override
        protected String getName() {
            return "Gradient printer";
        }

        @Override
        public String toString() {
            return super.toString() +
                    String.format(VERBOSE_FORMAT, "gradient level", gradientLvl);
        }

    }

    public static class RainbowPrinter extends AbsPrinter {
        public static float DEFAULT_COMPRESSION = 0.08f;
        public static float DEFAULT_SPREAD = 0.01f;
        public static float DEFAULT_FREQUENCY = 0.05f;

        protected Random random;
        protected float compression;
        protected float spread;
        protected float freq;
        protected float bias;

        public RainbowPrinter() {
            animationSpeed = 50;
            random = new Random();
            compression = DEFAULT_COMPRESSION;
            spread = DEFAULT_SPREAD;
            freq = DEFAULT_FREQUENCY;
        }

        public RainbowPrinter(Request request) {
            this();
            compression = request.args.size() > 0 ? Float.parseFloat(request.args.get(0)) :
                    randomRequested ? random.nextFloat() : compression;
            spread = request.args.size() >= 2 ? Float.parseFloat(request.args.get(1)) :
                    randomRequested ? random.nextFloat() : spread;
            freq = request.args.size() >= 3 ? Float.parseFloat(request.args.get(2)) :
                    randomRequested ? random.nextFloat() : freq;
        }

        protected void animate() {
            bias += 0.005f;
            freq = (float) Math.sin(bias);
        }

        @Override
        protected String getName() {
            return "Rainbow printer";
        }

        protected void build(List<String> lines, StringBuilder sb) {
            int ind = -1;
            String current;
            while (++ind < lines.size()) {
                current = lines.get(ind);
                char[] chars = current.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    float v = ind + i + (10 * freq) / spread;
                    float red = (float) (Math.sin(compression * v + 0) * 127 + 128);
                    float green = (float) (Math.sin(compression * v + 2 * (3.14 / 3)) * 127 + 128);
                    float blue = (float) (Math.sin(compression * v + 4 * (3.14 / 3)) * 127 + 128);
                    RGB rgb = new RGB(red, green, blue);
                    int xTermNumber = rgb.convertToXTermColor();
                    sb.append(String.format("\033[38;5;%dm%c\033[0m", xTermNumber, chars[i]));
                    if (debugRequested)
                        sb.append(rgb).append(" ").append("xTerm: ").append(xTermNumber).append(System.lineSeparator());
                }
                sb.append("\n");
            }
        }

        @Override
        public String toString() {
            return super.toString() +
                    String.format(VERBOSE_FORMAT, "compression", compression) +
                    String.format(VERBOSE_FORMAT, "spread", spread) +
                    String.format(VERBOSE_FORMAT, "freq", freq);
        }
    }

    public static class MonoPrinter extends AbsPrinter {
        private int startColor;
        private boolean direction;

        public MonoPrinter(Request request) {
            String color = request.args.size() > 0 ? request.args.get(0) :
                    randomRequested ? String.valueOf((16 + RANDOM.nextInt(255 - 16))) : "143";
            startColor = Integer.parseInt(color);
            direction = true;
        }

        @Override
        protected void build(List<String> lines, StringBuilder sb) {
            for (String var : lines)
                sb.append(String.format("\033[38;5;%dm%s\033[0m%n", startColor, var));
            if (debugRequested)
                sb.append(String.format("c: %-2s ", startColor));
        }

        @Override
        protected void animate() {
            if (startColor > 254 || startColor < 17) direction = !direction;
            startColor = getByDirection(direction, startColor);
        }

        @Override
        protected String getName() {
            return "Mono printer";
        }

        @Override
        public String toString() {
            return super.toString() +
                    String.format(VERBOSE_FORMAT, "color", startColor);
        }
    }

    /**
     * Read and collect all data from file
     */
    private static class FileAppender implements Appender {
        private final String file;

        public FileAppender(Request request) {
            file = request.args.get(0);
        }

        @Override
        public List<String> getResult() throws IOException {
            return Files.readAllLines(Paths.get(file));
        }

        @Override
        public String getName() {
            return "File appender";
        }

        @Override
        public String toString() {
            return print() + String.format(VERBOSE_FORMAT, "file", file);
        }
    }

    /**
     * Collect user input from app argument
     */
    public static class DefaultAppender implements Appender {
        private final String input;

        public DefaultAppender(Request request) {
            this.input = request.args.get(0);
        }

        @Override
        public List<String> getResult() throws IOException {
            return new ArrayList<>(Arrays.asList(input.split("\\n")));
        }

        @Override
        public String getName() {
            return "Default appender";
        }

        @Override
        public String toString() {
            return print() + String.format(VERBOSE_FORMAT, "input text", input);
        }
    }

    /**
     * Read, parse and collect all data from *.flf file
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

        public FlfAppender(Request request) {
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
                result.add(sb.toString().replaceAll("@@", "")
                        .replaceAll("@", "")
                        .replaceAll("\\$", ""));
                i++;
            }
        }

        @Override
        public String getName() {
            return "flf font appender";
        }

        @Override
        public String toString() {
            return print()
                    + String.format(VERBOSE_FORMAT, "font path", file)
                    + String.format(VERBOSE_FORMAT, "input text", input);
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

    /**
     * Base app arguments handler.
     * It scans all the arguments and argument options in a specific block and then pass control to another parser
     * [ mods - mods parser] -> [ printer - printer parser] -> [options - extension parser]
     */
    private static abstract class ArgumentHandler {
        protected ArgumentHandler next;
        protected Request request;
        protected Map<String, Request> localRequests;
        protected boolean requestFound;

        public ArgumentHandler() {
            init();
        }

        protected abstract void init();

        void handle(String[] args) {
            int off = 0;
            cx:
            for (String arg : args) {
                for (Map.Entry<String, Request> entry : localRequests.entrySet()) {
                    if ((arg.startsWith("-") && this.requestFound)) {
                        if (this.next != null) {
                            String[] arr = new String[args.length - off - 1];
                            System.arraycopy(args, off + 1, arr, 0, arr.length);
                            this.next.handle(arr);
                        }
                        return;
                    } else if (this.requestFound) {
                        off++;
                        this.request.add(arg);
                    }

                    if (!this.requestFound)
                        this.requestFound = resolve(entry.getValue(), arg);

                    if (this.requestFound)
                        continue cx;
                }
                if (!this.requestFound) {
                    handleUnrecognizedOption(arg);
                }
            }
        }

        protected void handleUnrecognizedOption(String arg) {
            System.out.println("Try to run Jfiglol --help");
        }

        protected boolean resolve(Request request, String arg) {
            for (String name : localRequests.get(request.names[0]).names) {
                if (arg.equals(name)) {
                    this.request = request;
                    return true;
                }
            }
            return false;
        }

        void setNext(ArgumentHandler next) {
            this.next = next;
        }

        public Map<String, Request> getLocalRequests() {
            return localRequests;
        }
    }

    /**
     * Detect requested mode (file, font or plain text)
     */
    private static class ModeHandler extends ArgumentHandler {
        private static final Map<String, Request> LOCAL = new LinkedHashMap<>(Map.of(
                "-p", new Request("{\"User input\"}",
                        DefaultAppender.class, "-p", "--plain"),
                "-f", new Request("{\"/path/to/font.flf\", \"User input\"}",
                        FlfAppender.class, "-f", "--font"),
                "-F", new Request("{\"/path/to/file\"}",
                        FileAppender.class, "-F", "--file"))
        );

        @Override
        protected void handleUnrecognizedOption(String arg) {
            System.out.println("Unrecognized mode option: " + arg);
            super.handleUnrecognizedOption(arg);
            System.exit(0);
        }

        protected void init() {
            localRequests = LOCAL;
        }

        public Appender getAppender() throws NoSuchMethodException, IllegalAccessException,
                InvocationTargetException, InstantiationException {
            return (Appender) this.request.clazz.getConstructor(Request.class)
                    .newInstance(this.request);
        }
    }

    /**
     * Detect requested printer (rainbow, gradient, mono or plain)
     */
    private static class PrinterHandler extends ArgumentHandler {
        private static final Map<String, Request> LOCAL = new LinkedHashMap<>(Map.of(
                "-m", new Request("{xTerm color (16...256)}", MonoPrinter.class,
                        "-m", "--mono"),
                "-g", new Request(String.format("{gradient level (default %d)}",
                        GradientPrinter.DEFAULT_GRADIENT),
                        GradientPrinter.class, "-g", "--gradient"),
                "-r", new Request(String.format("{compression (default %.2f)},\n" +
                                "                      {spread (default %.2f)},\n" +
                                "                      {frequency (default %.2f)}\n",
                        RainbowPrinter.DEFAULT_COMPRESSION,
                        RainbowPrinter.DEFAULT_SPREAD,
                        RainbowPrinter.DEFAULT_FREQUENCY),
                        RainbowPrinter.class, "-r", "--rainbow"))
        );

        @Override
        protected void handleUnrecognizedOption(String arg) {
            System.out.println("Unrecognized printer option: " + arg);
            super.handleUnrecognizedOption(arg);
            System.exit(0);
        }

        protected void init() {
            localRequests = LOCAL;
        }

        public Printer getPrinter() throws NoSuchMethodException, IllegalAccessException,
                InvocationTargetException, InstantiationException {
            return (Printer) this.request.clazz.getConstructor(Request.class)
                    .newInstance(this.request);
        }
    }

    /**
     * Detect requested app options (output animation, debug, current information or randomize all values)
     */
    private static class ExtendsHandler extends ArgumentHandler {
        private static final Map<String, Request> LOCAL = new LinkedHashMap<>(Map.of(
                "-a", new Request("(animated)", "-a", "--animated"),
                "-r", new Request("(random params)", "-r", "--random"),
                "-d", new Request("(app debug)", "-d", "--debug"),
                "-v", new Request("(current info)", "-v", "--verbose")
        ));

        @Override
        void handle(String[] args) {
            for (String arg : args) {
                for (Map.Entry<String, Request> entry : localRequests.entrySet()) {
                    Request request = entry.getValue();
                    String[] names = request.names;
                    for (String name : names) {
                        if (name.equals(arg)) {
                            GLOBAL_OPTIONS.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }

        protected void init() {
            localRequests = LOCAL;
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

        ModeHandler modeHandler = new ModeHandler();
        PrinterHandler printerHandler = new PrinterHandler();
        ExtendsHandler extendsHandler = new ExtendsHandler();

        modeHandler.setNext(printerHandler);
        printerHandler.setNext(extendsHandler);

        modeHandler.handle(args);

        Appender appender = modeHandler.getAppender();
        Printer printer = printerHandler.getPrinter();

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
        String format = "Usage: java Jfiglol [mode] [printer] [options]\n" +
                "mode:\n" + mode +
                "printer:\n" + printer +
                "options:\n" + options +
                "Examples: \n" +
                "         java Jfiglol -f \"./fonts/3d.flf\" \"Hello World!\" --rainbow 0.3 --animated\n" +
                "         java Jfiglol -p \"Hello World!\" --gradient 0.2\n" +
                "         java Jfiglol -F \"./examples/example.txt\" --mono 144\n" +
                "\n\n" +
                "Additions: Jfiglol [ --help | --palette ]\n" +
                "Examples:\n" +
                "         java Jfiglol --help\n" +
                "         java Jfiglol --palette\n" +
                "\n" +
                "With passing arguments via pipeline:\n" +
                "         echo \"Hello World!\" | xargs -I {} java Jfiglol --plain \"{}\" --rainbow --animated";
        new RainbowPrinter().print(new ArrayList<>(Arrays.asList(format.split("\\n"))));
    }
    
    private static String collect(Map<String, Request> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> {
            sb.append("         ");
            for (String arg : v.names) {
                sb.append(arg).append(" ");
            }
            sb.append(v.help);
            sb.append("\n");
        });
        return sb.toString();
    }
}
