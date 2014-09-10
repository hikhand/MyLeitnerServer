package ir.khaled.myleitner.Helper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import ir.khaled.myleitner.log.RequestLog;
import ir.khaled.myleitner.model.Card;
import ir.khaled.myleitner.model.Device;
import ir.khaled.myleitner.model.LastChanges;
import ir.khaled.myleitner.model.Leitner;
import ir.khaled.myleitner.model.Request;
import ir.khaled.myleitner.model.User;
import ir.khaled.myleitner.model.Welcome;
import ir.khaled.myleitner.response.Response;

import static ir.khaled.myleitner.Helper.Errors.EXCEPTION_ADD_CARD;
import static ir.khaled.myleitner.Helper.Errors.EXCEPTION_CREATE_LEITNER;
import static ir.khaled.myleitner.Helper.Errors.EXCEPTION_LOGIN_USER;
import static ir.khaled.myleitner.Helper.Errors.EXCEPTION_REGISTER_USER;
import static ir.khaled.myleitner.Helper.Errors.INVALID_REQUEST_OBJECT;
import static ir.khaled.myleitner.Helper.Errors.NO_SUCH_METHOD;
import static ir.khaled.myleitner.Helper.Errors.SQL_DEVICE;
import static ir.khaled.myleitner.Helper.Errors.UNKNOWN_DEVICE;
import static ir.khaled.myleitner.Helper.Util.exceptionTraceToString;

/**
 * Created by khaled.bakhtiari on 5/1/2014.
 */
public class RequestHandler {
    private static final String LAST_CHANGES = "lastChanges";
    private static final String WELCOME = "welcome";
    private static final String PING = "ping";
    private static final String REGISTER_DEVICE = "registerDevice";
    private static final String ADD_CARD = "addCard";
    private static final String LAST_CARDS = "lastCards";
    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String CREATE_LEITNER = "createLeitner";
    private static final String ASSIGN_TO_LEITNER = "assignToLeitner";

    private Request request;
    private OutputStreamWriter streamWriter;

    public RequestHandler(Request request, OutputStreamWriter streamWriter) {
        this.request = request;
        this.streamWriter = streamWriter;
    }

    /**
     * resolves a method from the request.
     *
     * @param isConnectionVerified if true connection won't verify
     * @return whether the request is verified or not.
     * @throws IOException on any failure
     */
    public boolean handle(boolean isConnectionVerified) throws IOException {
        if (request == null) {
            handleInvalidRequest();
            return false;
        }

        if (request.requestName.equals(PING)) {
            handlePing();
            return false;
        }

        //if the request is
        if (!request.requestName.equals(REGISTER_DEVICE) && !isConnectionVerified && !isDeviceAllowed())
            return false;

        long startTime = System.currentTimeMillis();
        String target = null;

        switch (request.requestName) {
            case LAST_CHANGES:
                handleLastChanges();
                break;

            case REGISTER_DEVICE:
                handleRegisterDevice();
                break;

            case WELCOME:
                handleWelcome();
                break;

            case ADD_CARD:
                handleAddCard();
                break;

            case LAST_CARDS:
                handleLastCards();
                break;

            case LOGIN:
                handleRequestLogin();
                target = request.getParamValue(User.PARAM_USERNAME);
                break;

            case REGISTER:
                handleRequestRegister();
                target = request.getParamValue(User.PARAM_EMAIL);
                break;

            case CREATE_LEITNER:
                handleCreateLeitner();
                target = request.getParamValue(Leitner.PARAM_NAME);
                break;

            case ASSIGN_TO_LEITNER:
                handleAssignToLeitner();
                break;

            default:
                handleNoSuchMethod();
                return true;
        }

        logRequest(target, Util.timeDistanceInt(startTime));

        return true;
    }

    private void logRequest(String target, int takenTime) throws IOException {
        try {
            RequestLog.saveLog(request.getUDK(), request.requestName, target, takenTime);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            response = Response.error(SQL_DEVICE, "error registerDevice. error: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    private void handleAddCard() throws IOException {
        try {
            Response<Boolean> response = Card.addCard(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = Response.error(EXCEPTION_ADD_CARD, "Exception addCard. error: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    /**
     * handle the request which gets a list of cards from database and returns to client
     */
    private void handleLastCards() throws IOException {
        try {
            Response<ArrayList<Card>> response = Card.getLastCards(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = Response.error(EXCEPTION_ADD_CARD, "Exception lastCards. error: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    /**
     * checks whether the device is valid or not if so returns true otherwise sends <b>response back to client</b> and returns false
     *
     * @return true if device is valid false otherwise
     * @throws IOException on any failure in socket connection
     */
    private boolean isDeviceAllowed() throws IOException {
        try {
            if (Device.isDeviceValid(request)) {
                return true;
            } else {
                Response response = Response.error(UNKNOWN_DEVICE, "the device is unknown!");
                response.sendResponse(streamWriter);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Response response = Response.error(SQL_DEVICE, "error performing SQL operation for deviceCheck. error: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
            return false;
        }
    }

    private void handleRequestLogin() throws IOException {
        try {
            Response<User> response = User.loginUser(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = Response.error(EXCEPTION_LOGIN_USER, "Exception while logging in user: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    private void handleRequestRegister() throws IOException {
        try {
            Response<User> response = User.register(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = Response.error(EXCEPTION_REGISTER_USER, "Exception while registering user: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    private void handleCreateLeitner() throws IOException {
        try {
            Response<Boolean> response = Leitner.create(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = Response.error(EXCEPTION_CREATE_LEITNER, "Exception while trying to create a leitner: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    private void handleAssignToLeitner() throws IOException {
        try {
            Response<Boolean> response = Card.assignToLeitner(request);
            response.sendResponse(streamWriter);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = Response.error(EXCEPTION_CREATE_LEITNER, "Exception while trying to assign card to leitner: " + exceptionTraceToString(e));
            response.sendResponse(streamWriter);
        }
    }

    /*
     * ERRORS
     */
    private void handleNoSuchMethod() throws IOException {
        Response response = Response.error(NO_SUCH_METHOD, "no such method exists to handle the request.");
        response.sendResponse(streamWriter);
    }

    private void handleInvalidRequest() throws IOException {
        Response response = Response.error(INVALID_REQUEST_OBJECT, "the request object is invalid and can't be handled");
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