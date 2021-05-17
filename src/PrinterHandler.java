import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Detect requested printer (rainbow, gradient, mono or plain)
 */
public class PrinterHandler extends ArgumentHandler {
    public static final Map<String, Request> LOCAL = new LinkedHashMap<>(Map.of(
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

    @Override
    protected Map<String, Request> init() {
        return LOCAL;
    }

    public Printer getPrinter() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return (Printer) this.request.getClazz().getConstructor(Request.class)
                .newInstance(this.request);
    }
}


