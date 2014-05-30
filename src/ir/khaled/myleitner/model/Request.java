package ir.khaled.myleitner.model;

import java.util.ArrayList;

/**
 * Created by kh.bakhtiari on 5/27/2014.
 */
public class Request {
    public ArrayList<Param> params;
    public String requestName;

    public static class Param {
        public String name;
        public String value;

        public Param(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public String getParamValue(String paramName) {
        if (params == null || params.size() == 0)
            return null;

        for (Param param : params) {
            if (param.name.equals(paramName)) {
                return param.value;
            }
        }
        return null;
    }

//    public static void sendResponse(OutputStreamWriter streamWriter, Response response) throws IOException, NullPointerException {
//        sendResponse(streamWriter, response, null);
//    }

//    public static void sendResponse(OutputStreamWriter streamWriter, Response response, Class<?> classType) throws IOException, NullPointerException {
//        if (streamWriter == null)
//            throw new NullPointerException("ObjectOutputStream parameter is null.");
//
//        if (response == null)
//            throw new NullPointerException("Response parameter is null.");
//
//        String json = response.getJson();
//        json += SocketConnection.EOF;
//        streamWriter.write(json, 0, json.length());
//        streamWriter.flush();
//    }
}
