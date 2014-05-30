package ir.khaled.myleitner.Socket;

import java.net.ServerSocket;
import java.util.ArrayList;

import ir.khaled.myleitner.Helper.LogHelper;

/**
 * Created by khaled.bakhtiari on 4/29/2014.
 */
public class SocketServer implements SocketConnection.ISocketConnectionListener {

    private static SocketServer instance;
    private ServerSocket mListener;
    private ArrayList<SocketConnection> mSocketConnections;


    public static SocketServer getInstance() {
        if (instance == null) {
            instance = new SocketServer();
        }
        return instance;
    }

    private SocketServer() {
        mSocketConnections = new ArrayList<SocketConnection>();
    }

    public void startServer() {
        if (mListener != null && !mListener.isClosed())
            return;

        try {
            mListener = new ServerSocket(44485);
            LogHelper.logD("Server started.");
            while (true) {
                SocketConnection socketConnection = new SocketConnection(mListener.accept(), mSocketConnections.size() + 1, this);
                addSocketConnection(socketConnection);
                socketConnection.start();
            }
        } catch (Exception e) {
            LogHelper.logD("Unfortunately server stopped working.");
            e.printStackTrace();
        } finally {
            try {
                if (mListener != null) {
                    mListener.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addSocketConnection(SocketConnection socketConnection) {
        mSocketConnections.add(socketConnection);
    }

    @Override
    public void onSocketConnectionEstablished(SocketConnection socketConnection) {}

    @Override
    public void onSocketConnectionClosed(SocketConnection socketConnection) {
        mSocketConnections.remove(socketConnection);
    }
}

