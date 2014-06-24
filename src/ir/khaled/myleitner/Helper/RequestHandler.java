package ir.khaled.myleitner.Helper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import ir.khaled.myleitner.model.Card;
import ir.khaled.myleitner.model.Device;
import ir.khaled.myleitner.model.LastChanges;
import ir.khaled.myleitner.model.Request;
import ir.khaled.myleitner.model.Welcome;
import ir.khaled.myleitner.response.Response;

/**
 * Created by khaled.bakhtiari on 5/1/2014.
 */
public class RequestHandler {
    private static final String METHOD_SIGN_UP = "signUp";
    private static final String METHOD_SIGN_IN = "signIn";
    private static final String METHOD_LAST_CHANGES = "lastChanges";
    private static final String METHOD_WELCOME = "welcome";
    private static final String METHOD_PING = "ping";
    private static final String METHOD_REGISTER_DEVICE = "registerDevice";
    private static final String METHOD_ADD_CARD = "addCard";

    private Request request;
    private OutputStreamWriter streamWriter;

    public RequestHandler(Request request, OutputStreamWriter streamWriter) {
        this.request = request;
        this.streamWriter = streamWriter;
    }


    /**
     * resolves a method from the request.
     * @param isConnectionVerified if true connection won't verify
     * @return whether the request is verified or not.
     * @throws IOException on any failure
     */
    public boolean handle(boolean isConnectionVerified) throws IOException {
        if (request == null) {
            handleInvalidRequest();
            return false;
        }

        if (request.requestName.equals(METHOD_PING)) {
            handlePing();
            return false;
        }

        if (!request.requestName.equals(METHOD_REGISTER_DEVICE) && !isConnectionVerified && !isDeviceAllowed())
            return false;


        if (request.requestName.equals(METHOD_LAST_CHANGES)) {
            handleLastChanges();
        } else if (request.requestName.equals(METHOD_WELCOME)) {
            handleWelcome();
        } else if (request.requestName.equals(METHOD_REGISTER_DEVICE)) {
            handleRegisterDevice();
        } else if (request.requestName.equals(METHOD_ADD_CARD)) {
            handleAddCard();
        } else {
            handleNoSuchMethod();
        }
        return true;
    }

    private void handleLastChanges() throws IOException {
        Response<LastChanges> response = LastChanges.handleRequest(request);
        response.sendResponse(streamWriter);
    }

    private void handleWelcome() throws IOException {
        Response<Welcome> response = Welcome.handleRequest(request);
        response.sendResponse(streamWriter);
    }

    private void handlePing() throws IOException {
        Response response = new Response();
        response.success = true;
        response.sendResponse(streamWriter);
    }

    private void handleRegisterDevice() throws IOException {
        Response response = null;
        try {
            response = Device.handleRegisterDevice(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(ErrorHelper.SQL_DEVICE, "error registerDevice. error : " + e.toString());
            response.sendResponse(streamWriter);
        }
    }

    private void handleAddCard() throws IOException {//TODO make it real
        try {
            Response<Boolean> response = Card.addCard(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(ErrorHelper.EXCEPTION_ADD_CARD, "Exception addCard. error : " + e.toString());
            response.sendResponse(streamWriter);
        }
    }

    private boolean isDeviceAllowed() throws IOException{
        boolean allowed;
        try {
            allowed = Device.isDeviceValid(request);
            if (allowed)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Response response = new Response(ErrorHelper.SQL_DEVICE, "error performing SQL operation for deviceCheck. error : " + e.toString());
            response.sendResponse(streamWriter);
            return false;
        }

        Response response = new Response(ErrorHelper.UNKNOWN_DEVICE, "the device is unknown!");
        response.sendResponse(streamWriter);
        return false;
    }




    /*
     * ERRORS
     */
    private void handleNoSuchMethod() throws IOException {
        Response response = new Response(ErrorHelper.NO_SUCH_METHOD, "no such method exists to handle the request.");
        response.sendResponse(streamWriter);
    }

    private void handleInvalidRequest() throws IOException {
        Response response = new Response(ErrorHelper.INVALID_REQUEST_OBJECT, "the request object is invalid and can't be handled");
        response.sendResponse(streamWriter);
    }


//            if (request.request.equals(METHOD_SIGN_UP)) {
//            if (request.result instanceof UserRegister) {
//                return User.register((UserRegister) request.result);
//            } else {
//                return getResponseInvalidObjectType();
//            }
//        } else if (request.request.equals(METHOD_SIGN_IN)) {
//            if (request.result instanceof UserLogin) {
//                return User.login((UserLogin) request.result);
//            } else {
//                return getResponseInvalidObjectType();
//            }
//        } else {
//            return new Response(ErrorHelper.NO_SUCH_METHOD, );
//        }
}