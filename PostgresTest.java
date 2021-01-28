import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PostgresTest {
    public static void main(String[] args) throws SQLException, InterruptedException, IOException, AWTException {
        int count = 0;
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 4; i++) {
                System.out.println("GRAAAAA");
		Thread.sleep(1000);
            }
            for (int i = 0; i < 4; i++) {
                System.out.printf("\033[%dA", 1); // Move up
                System.out.print("\033[2K");
                Thread.sleep(1000);
            }

            Thread.sleep(2000);
        }
    }

}
