package ir.khaled.myleitner.model;

import ir.khaled.myleitner.response.Response;

/**
 * Created by kh.bakhtiari on 5/29/2014.
 */
public class LastChanges {
    private String lastChanges;

    public LastChanges() {
        this.lastChanges = "this is the text of last changes which i think should be HTML";
    }

    public static Response<LastChanges> handleRequest(Request request) {
        Response<LastChanges> response = new Response<LastChanges>();
        response.success = true;
        response.result = new LastChanges();
        return response;
    }
}
