package com.owpk.handler;

import com.owpk.Jfiglol;
import com.owpk.Request;
import com.owpk.printer.GradientPrinter;
import com.owpk.printer.MonoPrinter;
import com.owpk.printer.Printer;
import com.owpk.printer.RainbowPrinter;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detect requested com.owpk.printer (rainbow, gradient, mono or plain)
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
        this.request = LOCAL.get("-r");
        if (next != null) {
            next.handle(Jfiglol.getArgs());
        }
    }

    @Override
    protected void addNeededArgs() {
    }

    @Override
    protected Map<String, Request> init() {
        return LOCAL;
    }

    public Printer getPrinter() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if (this.request == null) this.request = LOCAL.get("-r");
        return (Printer) this.request.getClazz().getConstructor(Request.class)
                .newInstance(this.request);
    }
}


