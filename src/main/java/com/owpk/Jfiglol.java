package com.owpk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.owpk.appender.FlfAppender;
import com.owpk.decorators.RainbowDecorator;
import com.owpk.renderer.RenderEngine;
import com.owpk.renderer.Renderable;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "jfiglol", description = "...",
        mixinStandardHelpOptions = true)
public class Jfiglol implements Runnable {

    @Option(names = {"-a", "--animate"})
    boolean animate;

    @Option(names = {"-f", "--frame-rate"}, defaultValue = "20")
    Integer frameRate;

    @Option(names = {"-F", "--file"}, description = "set user input as file name to print content from")
    boolean fileInput;

    @Option(names = {"-t", "--font"}, description = "set flf font file")
    String fontFile;

    @Parameters(index = "0", defaultValue = "", description = "user input")
    String input;


    public static void main(String[] args) throws Exception {
        PicocliRunner.run(Jfiglol.class, args);
    }

    public void run() {
        if (input.isBlank()) {
            picocli.CommandLine.usage(this, System.out);
            System.exit(0);
        }
        Renderable renderable;
        if (fileInput) {
            try {
                final var lines = Files.readAllLines(Paths.get(input));
                renderable = new RainbowDecorator(lines);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            var flfAppender = fontFile == null ?
                new FlfAppender(input) : new FlfAppender(input, fontFile);
            var composed = flfAppender.getResult().stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            renderable = new RainbowDecorator(() -> composed);
        }

        if (animate)
            RenderEngine.renderWithFixedFrameRate(renderable, frameRate);
        else
            RenderEngine.render(renderable);
    }
}
