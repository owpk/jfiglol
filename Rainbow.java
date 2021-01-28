import java.util.*;

public class Reader {
    private static final String[][][] colors256 = new String[6][][];
    private static void fillColors() {
        int ind = 16;
        for (int i = 0; i < colors256.length; i++) {
            colors256[i] = new String[6][6];
            for (int y = 0; y < colors256[i].length; y++) {
                for (int x = 0; x < colors256[i].length; x++) {
                    colors256[i][y][x] = String.format("%03d", ind++);
                }
            }
        }
    }
    public static void main(String[] args) {
        fillColors();

        List<String> lines = new ArrayList<>();
        lines.add("aaaaaaaaaaaaaaaaaaa");
        lines.add("aaaaaaaaaaaaaaaaaaa");
        lines.add("aaaaaaaaaaaaaaaaaaa");
        lines.add("aaaaaaaaaaaaaaaaaaa");
        lines.add("aaaaaaaaaaaaaaaaaaa");
//        red   = Math.sin(freq * i + 0) * 127 + 128;
//        green = Math.sin(freq * i + 2 * Math::PI/3) * 127 + 128;
//        blue  = Math.sin(freq * i + 4 * Math::PI/3) * 127 + 128;
        String current;
        int ind = -1;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int xInd = 0;
        while (ind++ < lines.size()) {
            current = lines.get(ind);
            char[] chars = current.toCharArray();
            for (char var : chars) {
                sb.append(
                    String.format("\033[38;5;%sm%c\033[0m%n",
                            colors256[1][(int) Math.round(Math.sin(xInd++ % 5))][(int) Math.round(Math.sin(xInd++ % 5))],
                            var));
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
    }
}
