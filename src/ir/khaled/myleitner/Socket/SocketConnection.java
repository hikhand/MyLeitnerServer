package ir.khaled.myleitner.socket;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ir.khaled.myleitner.Helper.LogHelper;
import ir.khaled.myleitner.Helper.RequestHandler;
import ir.khaled.myleitner.Helper.Util;
import ir.khaled.myleitner.model.Request;

/**
 * Created by khaled.bakhtiari on 4/29/2014.
 */
public class SocketConnection extends Thread {
    private static Gson gson = new Gson();
    public static final String EOF = "\u001a\uFFFF\u001A\uFFFF";
    private Socket mSocket;
    private int mSocketId;
    private ISocketConnectionListener mSocketConnectionListener;
    private OutputStreamWriter streamWriter;
    private InputStreamReader streamReader;

    /**
     * determines whether is the connection is verified.
     */
    private boolean isConnectionVerified;


    public SocketConnection(Socket socket, int socketId, ISocketConnectionListener connectionClosedListener) {
        mSocket = socket;
        mSocketId = socketId;
        mSocketConnectionListener = connectionClosedListener;
        LogHelper.logD("new client added with id:#" + mSocketId + " and Ip Address: " + mSocket.getInetAddress());
    }

    @Override
    public void run() {
        try {
            onConnectionEstablished();
        } catch (Exception e) {
            LogHelper.logD(mSocketId, "ip: " + mSocket.getInetAddress() + " eror: " + e.toString());
        } finally {
            try {
                mSocket.close();
            } catch (IOException e) {
                LogHelper.logD("Couldn't close the socket#" + mSocketId + ", what's going on? : " + e.toString());
            }
            mSocketConnectionListener.onSocketConnectionClosed(this);
            LogHelper.logD("Connection with client#" + mSocketId + " closed");
        }
    }

    public void onConnectionEstablished() throws Exception {
        mSocketConnectionListener.onSocketConnectionEstablished(this);
        streamReader = new InputStreamReader(mSocket.getInputStream());
        streamWriter = new OutputStreamWriter(mSocket.getOutputStream());

        while (true) {
            getRequest();
        }
    }

    private void getRequest() throws IOException {
        char[] buffer = new char[8192];
        String part, result = "";
        int length = 0;
        while ((length = streamReader.read(buffer)) != -1) {
            part = new String(buffer, 0, length);
            if (length >= 4 && part.substring(length - 4).equals(EOF)) {//if the outputStream is ended
                part = part.substring(0, length - 4);
                result += part;
                break;
            }
            result += part;
        }
        if (length == -1) {
            throw new IOException("stream from client has been closed.");
        }
        String[] results = result.split(EOF);
        for (String res : results) {
            if (res.length() > 0)
                handleRequest(res);
        }
    }

    private void handleRequest(String jsonRequest) throws IOException {
        LogHelper.logD(mSocketId, "handling request : " + jsonRequest);
        if (Util.isEmpty(jsonRequest)) {
            LogHelper.logD(mSocketId, "empty request, ignoring.");
            return;
        }

        Request request = getRequest(jsonRequest);
        if (request == null)
            return;

        isConnectionVerified = new RequestHandler(request, streamWriter).handle(isConnectionVerified);
    }

    private Request getRequest(String jsonRequest) {
        try {
            return gson.fromJson(jsonRequest, Request.class);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static interface ISocketConnectionListener {

        public void onSocketConnectionEstablished(SocketConnection socketConnection);

        public void onSocketConnectionClosed(SocketConnection socketConnection);

    }
}
