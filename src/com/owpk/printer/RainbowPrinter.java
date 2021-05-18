package com.owpk.printer;

import com.owpk.Jfiglol;
import com.owpk.RGB;
import com.owpk.Request;

import java.util.List;
import java.util.Random;

public class RainbowPrinter extends AbsPrinter {
    public static float DEFAULT_COMPRESSION = 0.08f;
    public static float DEFAULT_SPREAD = 0.01f;
    public static float DEFAULT_FREQUENCY = 0.05f;

    protected Random random;
    protected float compression;
    protected float spread;
    protected float freq;
    protected float bias;

    public RainbowPrinter() {
        animationSpeed = 50;
        random = new Random();
        compression = DEFAULT_COMPRESSION;
        spread = DEFAULT_SPREAD;
        freq = DEFAULT_FREQUENCY;
    }

    public RainbowPrinter(Request request) {
        this();
        compression = request.getArgs().size() > 0 ? Float.parseFloat(request.getArgs().get(0)) :
                randomRequested ? random.nextFloat() : compression;
        spread = request.getArgs().size() >= 2 ? Float.parseFloat(request.getArgs().get(1)) :
                randomRequested ? random.nextFloat() : spread;
        freq = request.getArgs().size() >= 3 ? Float.parseFloat(request.getArgs().get(2)) :
                randomRequested ? random.nextFloat() : freq;
    }

    protected void animate() {
        bias += 0.005f;
        freq = (float) Math.sin(bias);
    }

    @Override
    protected String getName() {
        return "Rainbow com.owpk.printer";
    }

    protected void build(List<String> lines, StringBuilder sb) {
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
                RGB rgb = new RGB(red, green, blue);
                int xTermNumber = rgb.convertToXTermColor();
                sb.append(String.format("\033[38;5;%dm%c\033[0m", xTermNumber, chars[i]));
                if (debugRequested)
                    sb.append(rgb).append(" ").append("xTerm: ").append(xTermNumber).append(System.lineSeparator());
            }
            sb.append("\n");
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(Jfiglol.VERBOSE_FORMAT, "compression", compression) +
                String.format(Jfiglol.VERBOSE_FORMAT, "spread", spread) +
                String.format(Jfiglol.VERBOSE_FORMAT, "freq", freq);
    }
}

