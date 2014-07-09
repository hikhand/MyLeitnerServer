import ir.khaled.myleitner.Helper.DatabaseHelper;
import ir.khaled.myleitner.Helper.LogHelper;
import ir.khaled.myleitner.socket.SocketServer;

/**
 * Created by kh.bakhtiari on 4/29/2014.
 */
public class MyDictionary {

    public static void main(String[] args) {
        LogHelper.logD("application started starting server.");

        DatabaseHelper.getInstance();

        SocketServer.getInstance().startServer();
    }
}

