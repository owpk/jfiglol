package com.owpk.renderer;

import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
public class RenderEngine {

    public static void render(Renderable renderable) {
        var renderContent = renderable.getRenderContent();
        System.out.print(renderContent);
    }

    public static void renderWithFixedFrameRate(Renderable renderable, Integer frameRate) {
        var sleepTime = 1000 / frameRate;
        try {
            while (true) {
                render(renderable);
                var completableFuture = CompletableFuture
                        .supplyAsync(() -> {
                            var height = getLinesHeightOfContent(renderable.getRenderContent());
                            return clearScreen(height);
                        });
                Thread.sleep(sleepTime);
                var clearContent = completableFuture.get();
                System.out.print(clearContent);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static Long getLinesHeightOfContent(String content) {
        return content.lines().count();
    }

    public static String clearScreen(Long height) {
        var writer = new StringWriter();
        for (int i = 0; i < height; i++) {
            writer.write(String.format("\u001b[%dA", 1));
            writer.write("\u001b[2K");
        }
        return writer.toString();
    }
}
