package ir.khaled.myleitner.Helper;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ir.khaled.myleitner.socket.SocketConnection;

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
    private static Logger getLoggerSocketConnection() {
        Logger logger = Logger.getLogger(SocketConnection.class.getName());
        logger.addHandler(getFileHandler(SocketConnection.class.getName()));
        logger.setLevel(Level.ALL);
        return logger;
    }

    private static FileHandler getFileHandler(String fileName) {
        try {
            return new FileHandler(fileName + ".%u.%g.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
