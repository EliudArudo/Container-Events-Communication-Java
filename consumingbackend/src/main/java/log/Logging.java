package log;

import interfaces.STATUS_TYPE;

public class Logging {
    public static void logStatusFileMessage(STATUS_TYPE status, String packageName, String functionName, String message) {
        System.out.printf("%s: %s : %s : %s\n", status, packageName, functionName, message);
    }
}


