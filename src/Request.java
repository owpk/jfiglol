import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User input requests
 */
public class Request {
    private String[] names;
    private List<String> args;
    private String help;
    private Class<?> clazz;

    public Request(String help, Class<?> clazz, String... names) {
        this(help, names);
        this.clazz = clazz;
    }

    public Request(String help, String... names) {
        this.names = names;
        this.help = help;
        args = new ArrayList<>();
    }

    public void add(String arg) {
        args.add(arg);
    }

    @Override
    public String toString() {
        return String.format(" --Request %s: args: %s", Arrays.toString(names), args);
    }

    public String[] getNames() {
       return names;
    }

    public List<String> getArgs() {
       return args;
    }

    public String getHelp() {
       return help;
    }

    public Class<?> getClazz() {
       return clazz;
    }
}

