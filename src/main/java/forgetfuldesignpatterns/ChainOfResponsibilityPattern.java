package forgetfuldesignpatterns;

import lombok.Setter;

/**
 * A demonstration of Chain Of Responsibility Pattern
 */

@Setter
abstract class CustomLogger {
    private CustomLogger nextLogger;
    public static int DEBUG = 0;
    public static int INFO = 1;
    public static int ERROR = 2;
    private int level;

    public CustomLogger(int level) {
        this.level = level;
    }

    public void log(String msg, int level) {
        if (level <= this.level) {
            write(msg);
        } else {
            nextLogger.log(msg, level);
        }
    }

    abstract void write(String msg);
}


class DebugLogger extends CustomLogger {

    public DebugLogger() {
        super(CustomLogger.DEBUG);
    }

    @Override
    void write(String msg) {
        System.out.println("DEBUG : " + msg);
    }
}

class InfoLogger extends CustomLogger {

    public InfoLogger() {
        super(CustomLogger.INFO);
    }

    @Override
    void write(String msg) {
        System.out.println("INFO : " + msg);
    }
}

class ErrorLogger extends CustomLogger {

    public ErrorLogger() {
        super(CustomLogger.ERROR);
    }

    @Override
    void write(String msg) {
        System.out.println("ERROR : " + msg);
    }
}


public class ChainOfResponsibilityPattern {

    public static void main(String[] args) {

        CustomLogger debugLogger = new DebugLogger();
        CustomLogger infoLogger = new InfoLogger();
        CustomLogger errorLogger = new ErrorLogger();
        debugLogger.setNextLogger(infoLogger);
        infoLogger.setNextLogger(errorLogger);
        debugLogger.log("Hi", CustomLogger.INFO);
        infoLogger.log("Hello", CustomLogger.INFO);

    }
}
