package ir.khaled.myleitner.Helper;

/**
 * Created by khaled.bakhtiari on 4/29/2014.
 */
public class LogHelper {
    public static final boolean isDebuggingMode = true;

    private LogHelper() {
    }


    public static void logD(String log) {
        if (!isDebuggingMode)
            return;

        System.out.println(log);
    }

    public static void logD(int clientId, String log) {
        if (!isDebuggingMode)
            return;

        System.out.println("client#" + clientId + " " + log);
    }
}
