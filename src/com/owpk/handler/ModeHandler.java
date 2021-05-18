package com.owpk.handler;

import com.owpk.Jfiglol;
import com.owpk.Request;
import com.owpk.appender.*;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

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
    protected void addNeededArgs() {
        Queue<String> args = Jfiglol.getArgs();
        for (String arg : args) {
            if (arg.startsWith("-")) break;
            this.request.addArg(arg);
        }
    }

    @Override
    protected void handleUnrecognizedOption(String arg) {
        if (arg.startsWith("-")) {

        } else {
            this.request = LOCAL.get("-f");
            this.request.addArg(arg);
            Queue<String> args = Jfiglol.getArgs();
            args.remove(arg);
            handleArgsWithNextHandler(args);
        }
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

