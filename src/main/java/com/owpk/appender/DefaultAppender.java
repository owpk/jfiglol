package com.owpk.appender;

import com.owpk.Jfiglol;
import com.owpk.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collect user input from app argument
 */
public class DefaultAppender implements Appender {
    private final String input;

    public DefaultAppender(Request request) {
        this.input = request.getArgs().get(0);
    }

    @Override
    public List<String> getResult() throws IOException {
        return new ArrayList<>(Arrays.asList(input.split("\\n")));
    }

    @Override
    public String getName() {
        return "Default com.owpk.appender";
    }

    @Override
    public String toString() {
        return print() + String.format(Jfiglol.VERBOSE_FORMAT, "input text", input);
    }
}

