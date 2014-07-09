package ir.khaled.myleitner.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;

import ir.khaled.myleitner.Helper.LogHelper;
import ir.khaled.myleitner.socket.SocketConnection;

/**
 * Created by khaled.bakhtiari on 4/29/2014.
 */
public class Response<T> implements Serializable {
    public boolean success;
    public int errorCode;
    public String message;
    public T result;

    public Response() {
    }

    /**
     * creates an instance of {@link Response} this could be used to send responses back to client.
     *
     * @param errorCode to be used in response
     * @param message   to be used in response
     */
    public static <T> Response<T> error(int errorCode, String message) {
        Response<T> response = new Response<T>();
        response.success = false;
        response.errorCode = errorCode;
        response.message = message;
        return response;
    }

    /**
     * creates a new instance which {@link #success} is true.
     *
     * @param result response's result
     */
    public static <T> Response<T> success(T result) {
        Response<T> response = new Response<T>();
        response.success = true;
        response.result = result;
        return response;
    }

    /**
     * send object to the outputStream throws exception on any failure.
     *
     * @param outputStream the outputStream to write response to.
     * @param response     the response to be sent to outputStream.
     * @throws IOException          this exception will be thrown on any failure in writing object to outputStream.
     * @throws NullPointerException this exception will be thrown whether outputStream or response is null.
     */
    public static void sendResponse(ObjectOutputStream outputStream, Response response) throws IOException, NullPointerException {
        if (outputStream == null)
            throw new NullPointerException("ObjectOutputStream parameter is null.");

        if (response == null)
            throw new NullPointerException("Response parameter is null.");

        outputStream.writeObject(response);
    }

    public String getJson() {
        Type myType = new TypeToken<Response<T>>() {
        }.getType();
        return new Gson().toJson(this, myType);
    }

    public void sendResponse(OutputStreamWriter streamWriter) throws IOException {
        String json = getJson();
        LogHelper.logD("sending response: " + json);
        json += SocketConnection.EOF;
        streamWriter.write(json);
        streamWriter.flush();
    }
}
