import java.util.Map;

/**
 * Base app arguments handler.
 * It scans all the arguments and argument options in a specific block and then pass control to another parser
 * [ mods - mods parser] -> [ printer - printer parser] -> [options - extension parser]
 */
public abstract class ArgumentHandler {
    protected ArgumentHandler next;
    protected Request request;
    protected Map<String, Request> localRequests;
    protected boolean requestFound;

    public ArgumentHandler() {
        localRequests = init();
    }

	/**
	* Must initialize 'localRequests' map with some args
	*/
    protected abstract Map<String, Request> init();

    public void handle(String[] args) {
        int off = 0;
        cx:
        for (String arg : args) {
            for (Map.Entry<String, Request> entry : localRequests.entrySet()) {
                if ((arg.startsWith("-") && this.requestFound)) {
                    if (this.next != null) {
                        String[] arr = new String[args.length - off - 1];
                        System.arraycopy(args, off + 1, arr, 0, arr.length);
                        this.next.handle(arr);
                    }
                    return;
                } else if (this.requestFound) {
                    off++;
                    this.request.add(arg);
                }

                if (!this.requestFound)
                    this.requestFound = resolve(entry.getValue(), arg);

                if (this.requestFound)
                    continue cx;
            }
            if (!this.requestFound) {
                handleUnrecognizedOption(arg);
            }
        }
    }

    protected void handleUnrecognizedOption(String arg) {
        System.out.println("Try to run Jfiglol --help");
    }

    protected boolean resolve(Request request, String arg) {
        for (String name : localRequests.get(request.getNames()[0]).getNames()) {
            if (arg.equals(name)) {
                this.request = request;
                return true;
            }
        }
        return false;
    }

    public void setNext(ArgumentHandler next) {
        this.next = next;
    }

    public Map<String, Request> getLocalRequests() {
        return localRequests;
    }
}

