import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detect requested mode (file, font or plain text)
 */
public class ModeHandler extends ArgumentHandler {
    public static final Map<String, Request> LOCAL = new LinkedHashMap<>(Map.of(
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

    protected Map<String, Request> init() {
        return LOCAL;
    }

    public Appender getAppender() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return (Appender) this.request.getClazz().getConstructor(Request.class)
                .newInstance(this.request);
    }
}

