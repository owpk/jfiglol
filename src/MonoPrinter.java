import java.util.List;

public class MonoPrinter extends AbsPrinter {
    private int startColor;
    private boolean direction;

    public MonoPrinter(Request request) {
        String color = request.getArgs().size() > 0 ? request.getArgs().get(0) :
                randomRequested ? String.valueOf((16 + Jfiglol.RANDOM.nextInt(255 - 16))) : "143";
        startColor = Integer.parseInt(color);
        direction = true;
    }

    @Override
    protected void build(List<String> lines, StringBuilder sb) {
        for (String var : lines)
            sb.append(String.format("\033[38;5;%dm%s\033[0m%n", startColor, var));
        if (debugRequested)
            sb.append(String.format("c: %-2s ", startColor));
    }

    @Override
    protected void animate() {
        if (startColor > 254 || startColor < 17) direction = !direction;
        startColor = getByDirection(direction, startColor);
    }

    @Override
    protected String getName() {
        return "Mono printer";
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(Jfiglol.VERBOSE_FORMAT, "color", startColor);
    }
}

