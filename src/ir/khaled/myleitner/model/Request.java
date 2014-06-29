package ir.khaled.myleitner.model;

import java.util.HashMap;

/**
 * Created by kh.bakhtiari on 5/27/2014.
 */
public class Request {
    public HashMap<String, String> params;
    public String requestName;

    public String getParamValue(String paramName) {
        if (params == null || params.size() == 0)
            return null;

        return params.get(paramName);
    }

    @Override
    public String toString() {
        return "request: " + requestName + "params: " + params.toString();
    }
}
