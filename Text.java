import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class Text {
    private static final String VERBOSE_FORMAT = "%-15s: %s%n";
    private static final String[][][] colors256 = new String[6][][];
    private static int gradientLvl = 2;
    private static String input;
    private static Appender appender;

    private static final Map<String, Request> appRequests = new LinkedHashMap<>(Map.of(
            "-g", new Request("gradient level", x -> String.valueOf(gradientLvl), "-g", "--gradient"),

            "-v", new Request("verbose", x -> String.valueOf(x.requested), "-v", "--verbose"),

            "-m", new Request("mono color", x -> x.args.get(0), "-m", "--mono"),

            "-r", new Request("rainbow", x -> x.args.get(0), "-r", "--rainbow"),

            "-p", new Request("plain text", x -> String.valueOf(x.requested), "-p", "--plain")
    ));

    static {
        fillColors();
    }

    private static class Request {
        String[] names;
        boolean requested;
        List<String> args;
        String description;
        Function<Request, String> function;

        Request(String description,
                Function<Request, String> function, String... names) {
            this.description = description;
            this.names = names;
            this.function = function;
            args = new ArrayList<>();
        }

        void add(String arg) {
            args.add(arg);
        }

        void setRequested() {
            requested = true;
        }
    }

    public interface Appender {
        List<String> getResult() throws IOException;
    }

    public static class FileAppender implements Appender {
        private static final HashMap<Integer, Integer> OFFSET_MAP = new LinkedHashMap<>();
        private final String file;
        private List<String> lines;
        private int height;
        private int heightD;
        private int lineLength;
        private int mode;
        private int comments;

        public FileAppender(String file) {
            this.file = file;
        }

        /**
         *  flf metadata pattern: [flf2a$ a b c d e
         *  flf2 - "magic number" for file identification
         *  a    - should always be `a', for now
         *  $    - the "hardblank" -- prints as a blank, but can't be smushed
         *  a    - height of a character
         *  b    - height of a character, not including descenders
         *  c    - max line length (excluding comment lines) + a fudge factor
         *  d    - default smushmode for this font
         *  e    - number of comment lines - note that first metaline not include in this number
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
            sb.append(lines.get(seekPosition).replaceAll("$", " "));
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

    public static void main(String... args) throws Exception {
        appRequests.forEach((k, v) -> resolve(v, args));
        appender = getAppender(args[0]);
        input = args[1];

        if (appRequests.get("-v").requested) {
            appRequests.forEach((k, v) -> {
                if (v.requested) {
                    System.out.printf(VERBOSE_FORMAT, v.description,
                            v.function.apply(v));
                }
            });
            System.out.printf(VERBOSE_FORMAT, "input text", input);
            System.out.println();
        }

        if (appRequests.get("-g").requested) {
            gradientLvl = Integer.parseInt(appRequests.get("-g").args.get(0));
        }
        List<String> resultOutput = appender.getResult();
        print(resultOutput);
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

    private static void resolve(Request request, String... args) {
        cx:
        for (String var : args) {
            for (String name : appRequests.get(request.names[0]).names) {
                if (var.equals(name)) {
                    request.setRequested();
                    continue cx;
                }
            }

            if (var.startsWith("-") && request.requested)
                break;

            if (request.requested)
                request.args.add(var);
        }
    }


    private static Appender getAppender(String arg) {
        if (arg.equals("--plain") || arg.equals("-p"))
            return () -> new ArrayList<>(Arrays.asList(input.split("\n")));
        else return new FileAppender(arg);
    }

    private static void print(List<String> resultOutput) {
        if (appRequests.get("-r").requested)
            rainbowOutput(resultOutput);
        else if (appRequests.get("-g").requested)
            gradientOutput(resultOutput);
        else if (appRequests.get("-m").requested)
            monoColorOutput(resultOutput);
        else simpleOutput(resultOutput);
    }

    public static void rainbowOutput(List<String> lines) {
        Random random = new Random();
        String current;
        int ind = -1;
        StringBuilder sb = new StringBuilder();

        int total = 0;

        float compression = 0.08f;        
        float spread = 0.01f; 
        // random.nextInt(10) * 0.01f;
        float prec = 0.05f;

        Request req = appRequests.get("-r");

        if (req.requested) {
            if (req.args.size() > 0) {
                spread = Float.parseFloat(req.args.get(0));    
            }
        }

        while (++ind < lines.size()) {
            current = lines.get(ind);

            char[] chars = current.toCharArray();
            for (int i = 0; i < chars.length; i++) {

                float v = total + i + (10 * prec) / spread;

                float red = (float) (Math.sin(compression * v + 0) * 127 + 128);
                float green = (float) (Math.sin(compression * v + 2 * (3.14 / 3)) * 127 + 128);
                float blue = (float) (Math.sin(compression * v + 4 * (3.14 / 3)) * 127 + 128);

                RGB rgb = new RGB(red, green, blue);

                int xTermNumber = rgb.convertToXTermColor();

                sb.append(String.format("\033[38;5;%dm%c\033[0m", xTermNumber, chars[i]));
                if (appRequests.get("-v").requested)
                    sb.append(rgb).append(" ").append("xTerm: ").append(xTermNumber).append(System.lineSeparator());
            }
            total++;
            sb.append("\n");
        }
        System.out.print(sb.toString());
    }

    public static void gradientOutput(List<String> lines) {
        Random random = new Random();
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

            if (appRequests.get("-v").requested)
                System.out.printf("l %2s: x %s : y %s ", count++, xInd, yInd);

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

    public static void simpleOutput(List<String> lines) {
        int count = 0;
        boolean verb = appRequests.get("-v").requested;

        for (String var : lines) {
            if (verb)
                System.out.printf("l: %-2s ", count++);
            System.out.println(var);
        }
    }

    public static void monoColorOutput(List<String> lines) {
        String color = appRequests.get("-m").args.get(0);
        int count = 0;
        boolean verb = appRequests.get("-v").requested;

        for (String var : lines) {
            if (verb)
                System.out.printf("l: %-2s ", count++);
            System.out.printf("\033[38;5;%sm%s\033[0m%n", color,
                    var);
        }
    }
}
