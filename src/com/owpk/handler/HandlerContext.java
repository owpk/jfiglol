package com.owpk.handler;

import com.owpk.appender.Appender;
import com.owpk.printer.Printer;

import java.lang.reflect.InvocationTargetException;
import java.util.Queue;

public class HandlerContext {
    private ModeHandler modeHandler;
    private PrinterHandler printerHandler;
    private ExtendsHandler extendsHandler;
    private Queue<String> argsStack;

    public HandlerContext(Queue<String> args) {
        this.argsStack = args;
        this.modeHandler = new ModeHandler();
        this.printerHandler = new PrinterHandler();
        this.extendsHandler = new ExtendsHandler();
        this.modeHandler.setNext(printerHandler);
        this.printerHandler.setNext(extendsHandler);
        this.modeHandler.handle(argsStack);
    }

    public Appender getAppender() {
        try {
            return modeHandler.getAppender();
        } catch (InvocationTargetException | NoSuchMethodException
                | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public Printer getPrinter() {
        try {
            return printerHandler.getPrinter();
        } catch (InvocationTargetException | NoSuchMethodException
                | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public Queue<String> getArgsStack() {
        return argsStack;
    }
}
