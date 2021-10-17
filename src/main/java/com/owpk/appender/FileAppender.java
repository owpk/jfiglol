package com.owpk.appender;

import com.owpk.Jfiglol;
import com.owpk.Request;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Read and collect all data from file
 */
public class FileAppender implements Appender {
    private final String file;

    public FileAppender(Request request) {
        file = request.getArgs().get(0);
    }

    @Override
    public List<String> getResult() throws IOException {
        return Files.readAllLines(Paths.get(file));
    }

    @Override
    public String getName() {
        return "File com.owpk.appender";
    }

    @Override
    public String toString() {
        return print() + String.format(Jfiglol.VERBOSE_FORMAT, "file", file);
    }
}

