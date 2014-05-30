package ir.khaled.myleitner.Socket;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ir.khaled.myleitner.Helper.LogHelper;
import ir.khaled.myleitner.Helper.RequestHandler;
import ir.khaled.myleitner.model.Request;

/**
 * Created by khaled.bakhtiari on 4/29/2014.
 */
public class SocketConnection extends Thread {
    public static final String EOF = "\u001a\uFFFF\u001A\uFFFF";
    private Socket mSocket;
    private int mSocketId;
    private ISocketConnectionListener mSocketConnectionListener;
    private OutputStreamWriter streamWriter;
    private InputStreamReader streamReader;
    private BufferedReader bufferedReader;

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
        bufferedReader = new BufferedReader(streamReader);

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
        if (result.length() < 2) {
            handleRequest(result);
        } else {
            for (String res : results) {
                handleRequest(res);
            }
        }
    }

    /**
     * not using it because stocking in readLine when no \n !
     */
    private String getRequest2() throws IOException {
        String line, result = "";
        int lineLength;
        while ((line = bufferedReader.readLine()) != null) {
            lineLength = line.length();
            if (lineLength >= 4 && line.substring(lineLength - 4).equals(EOF)) {//if the outputStream is ended
                line = line.substring(0, lineLength - 4);
                result += '\n' + line;
                break;
            }

            if (result.length() == 0)
                result += line;
            else result += '\n' + line;
        }
        return result;
    }

    private void handleRequest(String jsonRequest) throws IOException {
        LogHelper.logD(mSocketId, "handling request : " + jsonRequest);
        if (jsonRequest == null || jsonRequest.length() == 0) {
            LogHelper.logD(mSocketId, "empty request, ignoring.");
            return;
        }

        Request request = new Gson().fromJson(jsonRequest, Request.class);

        isConnectionVerified = new RequestHandler(request, streamWriter).handle(isConnectionVerified);
    }

    public static interface ISocketConnectionListener {

        public void onSocketConnectionEstablished(SocketConnection socketConnection);

        public void onSocketConnectionClosed(SocketConnection socketConnection);

    }
}
