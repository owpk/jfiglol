package com.owpk.printer;

import com.owpk.Jfiglol;
import com.owpk.Request;

import java.util.List;

public class GradientPrinter extends AbsPrinter {
    public static int DEFAULT_GRADIENT = 2;
    protected int colorBucket;
    protected int yInd;
    protected int xInd;
    protected boolean xDirection;
    protected boolean yDirection;
    protected int gradientLvl;

    public GradientPrinter(Request request) {
        xInd = 1;
        xDirection = true;
        yDirection = true;
        gradientLvl = request.getArgs().size() > 0 ? Integer.parseInt(request.getArgs().get(0)) :
                randomRequested ? Jfiglol.RANDOM.nextInt(5) + 1 : DEFAULT_GRADIENT;
        colorBucket = randomRequested ? Jfiglol.RANDOM.nextInt(Jfiglol.COLORS_256.length) : 0;
        animationSpeed = 100;
    }

    @Override
    protected void build(List<String> lines, StringBuilder sb) {
        int ind = -1;
        String current;
        int gradient = 0;
        while (++ind < lines.size()) {
            current = lines.get(ind);
            if (xInd >= Jfiglol.COLORS_256.length || xInd <= 0) {
                yInd = getByDirection(yDirection, yInd);
                xDirection = !xDirection;
                xInd = xDirection ? 1 : Jfiglol.COLORS_256.length - 1;
            }

            if (yInd >= Jfiglol.COLORS_256.length || yInd < 0) {
                yDirection = !yDirection;
                colorBucket = (colorBucket + 1) % Jfiglol.COLORS_256.length;
                yInd = yDirection ? 0 : 5;
                xDirection = !xDirection;
                xInd = xDirection ? 1 : Jfiglol.COLORS_256.length - 1;
            }

            if (debugRequested)
                sb.append(String.format("b %d : x %2s: y %2s c: %s ", 
						colorBucket, xInd, yInd, Jfiglol.COLORS_256[colorBucket][yInd][xInd]));

            sb.append(String.format("\033[38;5;%sm%s\033[0m%n",
                    Jfiglol.COLORS_256[colorBucket][yInd][xInd],
                    current));
            gradient++;
            if (gradient % gradientLvl == 0 && gradient != 0) {
                xInd = getByDirection(xDirection, xInd);
            }
        }
    }

    @Override
    protected void animate() {
        xInd = getByDirection(xDirection, xInd);
    }

    @Override
    protected String getName() {
        return "Gradient com.owpk.printer";
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(Jfiglol.VERBOSE_FORMAT, "gradient level", gradientLvl);
    }

}

