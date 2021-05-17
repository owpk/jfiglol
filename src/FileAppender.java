import java.util.List;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Read and collect all data from file
 */
public class FileAppender implements Appender {
    private final String file;

    public FileAppender(Request request) {
        file = request.getArgs().get(0);
    }

    @Override
    public List<String> getResult() throws IOException {
        return Files.readAllLines(Paths.get(file));
    }

    @Override
    public String getName() {
        return "File appender";
    }

    @Override
    public String toString() {
        return print() + String.format(Jfiglol.VERBOSE_FORMAT, "file", file);
    }
}

