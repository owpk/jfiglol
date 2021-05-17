import java.util.List;

/**
 * Abstract printer, base output printer class contains method
 * to print and/or animate output
 */
public abstract class AbsPrinter implements Printer {
    protected boolean debugRequested;
    protected boolean randomRequested;
    protected boolean animated;
    protected int animationSpeed;

    public AbsPrinter() {
        debugRequested = Jfiglol.GLOBAL_OPTIONS.containsKey("-d");
        animated = Jfiglol.GLOBAL_OPTIONS.containsKey("-a");
        randomRequested = Jfiglol.GLOBAL_OPTIONS.containsKey("-r");
    }

    @Override
    public void print(List<String> lines) {
        if (animated) animatedPrint(lines);
        else simplePrint(lines);
    }

    protected void animatedPrint(List<String> lines) {
        try {
            while (true) {
                StringBuilder sb = new StringBuilder();
                build(lines, sb);
                animate();
                System.out.print(sb.toString() + "\n\n");
                Thread.sleep(animationSpeed == 0 ? 100 : animationSpeed);
                for (int i = 0; i < lines.size() + 2; i++) {
                    System.out.printf("\033[%dA", 1);
                    System.out.print("\033[2K");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void simplePrint(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        build(lines, sb);
        System.out.print(sb.toString());
    }

    protected int getByDirection(boolean direction, int input) {
        return direction ? ++input : --input;
    }

    protected abstract void build(List<String> lines, StringBuilder sb);

    protected abstract void animate();

    protected abstract String getName();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Jfiglol.VERBOSE_FORMAT, "printer name", getName()));
        sb.append(String.format(Jfiglol.VERBOSE_FORMAT, "debug", debugRequested));
        sb.append(String.format(Jfiglol.VERBOSE_FORMAT, "random values", randomRequested));
        sb.append(String.format(Jfiglol.VERBOSE_FORMAT, "animation", animated));
        if (animated)
            sb.append(String.format(Jfiglol.VERBOSE_FORMAT, "animation speed", animationSpeed));
        return sb.toString();
    }
}

