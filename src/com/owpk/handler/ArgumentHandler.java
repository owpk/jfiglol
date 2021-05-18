package com.owpk.handler;

import com.owpk.Jfiglol;
import com.owpk.Request;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * Base app arguments com.owpk.handler.
 * It scans all the arguments and argument options in a specific block and then pass control to another parser
 * [ mods - mods parser] -> [ printer - printer parser] -> [options - extension parser]
 */
public abstract class ArgumentHandler {
    protected ArgumentHandler next;
    protected Request request;
    protected Map<String, Request> localRequests;

    public ArgumentHandler() {
        localRequests = init();
    }

	/**
	* Must initialize 'localRequests' map with some args
	*/
    protected abstract Map<String, Request> init();

    protected void addNeededArgs() {
        Queue<String> queue = Jfiglol.getArgs();
        this.request.addArg(queue.peek());
    }

    public void handle(Queue<String> args) {
        Map<String, Request> availableArgs = getLocalRequests();
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext() && this.request == null) {
            String arg = iterator.next();
            if (isArgContains(arg, availableArgs)) {
                iterator.remove();
                addNeededArgs();
                handleArgsWithNextHandler(args);
            } else
                handleUnrecognizedOption(arg);
        }
    }

    protected void handleArgsWithNextHandler(Queue<String> args) {
        if (this.next != null) {
            next.handle(args);
        }
    }

    protected boolean isArgContains(String arg, Map<String, Request> availableArgs) {
        for (Map.Entry<String, Request> entry: availableArgs.entrySet()) {
            for (String name : entry.getValue().getNames()) {
                if (name.equals(arg)) {
                    this.request = entry.getValue();
                    return true;
                }
            }
        }
        return false;
    }

    protected void handleUnrecognizedOption(String arg) {
        System.out.println("Try to run jfiglol --help");
    }

    public void setNext(ArgumentHandler next) {
        this.next = next;
    }

    protected Map<String, Request> getLocalRequests() {
        return localRequests;
    }
}