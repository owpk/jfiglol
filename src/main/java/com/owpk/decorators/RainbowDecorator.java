package com.owpk.decorators;

import com.owpk.renderer.Renderable;
import com.owpk.utils.RGB;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
public class RainbowDecorator implements Renderable {
    public static float DEFAULT_COMPRESSION = 0.08f;
    public static float DEFAULT_SPREAD = 0.01f;
    public static float DEFAULT_FREQUENCY = 0.05f;
    protected Random random;
    protected float compression;
    protected float spread;
    protected float freq;
    protected float bias;
    private List<String> lines;


    public RainbowDecorator(Renderable renderable) {
        this(Arrays.asList(
                renderable.getRenderContent().split(System.lineSeparator())));
    }

    public RainbowDecorator(List<String> liens) {
        this.random = new Random();
        this.compression = DEFAULT_COMPRESSION;
        this.spread = DEFAULT_SPREAD;
        this.freq = DEFAULT_FREQUENCY;
        this.lines = liens;
    }

    @Override
    public String getRenderContent() {
        var content = decorateContent();
        bias += 0.005f;
        freq = (float) Math.sin(bias);
        return content;
    }

    private String decorateContent() {
        var sb = new StringBuilder();
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
                var rgb = new RGB(red, green, blue);
                int xTermNumber = rgb.convertToXTermColor();
                sb.append(String.format("\033[38;5;%dm%c\033[0m", xTermNumber, chars[i]));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
