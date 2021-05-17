import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Detect requested app options (output animation, debug, current information or randomize all values)
 */
public class ExtendsHandler extends ArgumentHandler {
    public static final Map<String, Request> LOCAL = new LinkedHashMap<>(Map.of(
            "-a", new Request("(animated)", "-a", "--animated"),
            "-r", new Request("(random params)", "-r", "--random"),
            "-d", new Request("(app debug)", "-d", "--debug"),
            "-v", new Request("(current info)", "-v", "--verbose")
    ));

    @Override
    public void handle(String[] args) {
        for (String arg : args) {
            for (Map.Entry<String, Request> entry : localRequests.entrySet()) {
                Request request = entry.getValue();
                String[] names = request.getNames();
                for (String name : names) {
                    if (name.equals(arg)) {
                        Jfiglol.GLOBAL_OPTIONS.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    protected Map<String, Request> init() {
        return LOCAL;
    }
}

