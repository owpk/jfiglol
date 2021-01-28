public class Colors {

    public static void main(String[] args) {
        String[][][] colors = new String[6][][];
        int ind = 16;
 
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new String[6][6];
            for (int y = 0; y < colors[i].length; y++) {
                for (int x = 0; x < colors[i].length; x++) {
                    colors[i][y][x] = String.format("%03d", ind++);
                }
            }
        }
 
        for (String[][] color : colors) {
        
            for (String[] strings : color) {
                for (int j = 0; j < color.length; j++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\033[38;5;")
                            .append(strings[j]).append("m")
                            .append(strings[j])
                            .append("\033[0m");
                    System.out.print(sb.toString() + " ");
                }
                System.out.println();
            }
        }
        
    }
 }