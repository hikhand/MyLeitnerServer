package ir.khaled.myleitner.model;

import ir.khaled.myleitner.response.Response;

/**
 * Created by kh.bakhtiari on 5/29/2014.
 */
public class Welcome {
    private String message;

    public Welcome() {
        this.message = "This is our welcome text!, so welcome to Your Leitner";
    }

    public static Response<Welcome> handleRequest(Request request) {
        Response<Welcome> response = new Response<Welcome>();
        response.success = true;
        response.result = new Welcome();
        return response;
    }
}
