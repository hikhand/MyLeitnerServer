package ir.khaled.myleitner.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ir.khaled.myleitner.Helper.Errors;
import ir.khaled.myleitner.Helper.Statements;
import ir.khaled.myleitner.Helper.Util;
import ir.khaled.myleitner.response.Response;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class User {
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_DISPLAY_NAME = "displayName";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_EMAIL = "email";
    public static final int NO_USER = 0;
    public static final int PASSWORD_MIN_LENGTH = 6;

    public int id;
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    public String picture;
    public Biography biography;
    public Device device;
    public String password;

    public static int getUserId(String udk) throws SQLException {
        PreparedStatement statement = Statements.getUserId();
        statement.setInt(1, Device.getDeviceId(udk));

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.first()) {
            return resultSet.getInt("USER_ID");//column USER_ID from table user_device. if 0 returned means NO_USER
        } else {
            return NO_USER;
        }
    }

    public static Response<User> loginUser(Request request) throws SQLException {
        String username = request.getParamValue(PARAM_USERNAME);
        String password = request.getParamValue(PARAM_PASSWORD);

        if (Util.isEmpty(username))
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_USERNAME) + "(as for now is email)");
        if (Util.isEmpty(password))
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_PASSWORD));

        Object object = validateUser(username, password, false);
        if (object instanceof User) {//user successfully logged in
            User loggedInUser = (User) object;
            assignDeviceAndUser(loggedInUser.id, request.getUDK());
            return Response.success(loggedInUser);
        } else if (object instanceof Integer) {//no user with the given username or email exists
            return Response.error(Errors.USER_DOESNT_EXIST, "no user with the given username or email exists");
        } else {//username and password didn't match
            return Response.error(Errors.WRONG_USERNAME_PASSWORD, "username or password is wrong");
        }
    }

    public static Response<User> register(Request request) throws SQLException {
        String udk = request.getUDK();
        String displayName = request.getParamValue(PARAM_DISPLAY_NAME);
        String password = request.getParamValue(PARAM_PASSWORD);
        String email = request.getParamValue(PARAM_EMAIL);

        if (Util.isEmpty(udk))//if param udk is missing from request
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam("udk"));
        if (Util.isEmpty(displayName))//if param displayName is missing from request
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_DISPLAY_NAME));
        if (Util.isEmpty(email))//if param email is missing from request
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_EMAIL));
        if (Util.isEmpty(password))//if param password is missing from request
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_PASSWORD));
        if (password.length() < 6)//if password is not strong enough
            return Response.error(Errors.REGISTER_WEAK_PASSWORD, "password length is less than " + PASSWORD_MIN_LENGTH);

        //if everything from the request is alright.
        //if an user with the given email exists
        if (userAlreadyExist(email))
            return Response.error(Errors.REGISTER_EMAIL_ALREADY_EXISTS, "an user with the given email already exists");

        User user = new User();
        user.email = email;
        user.displayName = displayName;
        user.password = password;
        user.saveUser(udk);

        Object object = validateUser(email, password, false);
        if (object instanceof User) {//user successfully logged in
            User loggedInUser = (User) object;
            assignDeviceAndUser(loggedInUser.id, request.getUDK());
            return Response.success(loggedInUser);
        } else if (object instanceof Integer) {//no user with the given username or email exists
            return Response.error(Errors.USER_DOESNT_EXIST, "no user with the given username or email exists");
        } else {//username and password didn't match
            return Response.error(Errors.WRONG_USERNAME_PASSWORD, "username or password is wrong");
        }
    }

    /**
     * checks whether user is logged in on the asked device or not.
     * @param userId the user to be check whether is logged in or not
     * @param udk the device to check if user is logged in on or not.
     * @return true if the asked user is logged in on the device.
     * @throws SQLException on any sql failure
     */
    public static boolean isUserLoggedIn(int userId, String udk) throws SQLException {
        PreparedStatement statement = Statements.loginCheck();
        statement.setString(1, udk);

        ResultSet resultSet = statement.executeQuery();

        //if result set is empty meaning no user is login return false.
        if (!resultSet.next())
            return false;

        //if the logged in user id is the same as the asked one then return true.
        int loggedInUserId = resultSet.getInt("USER_ID");
        return loggedInUserId == userId;
    }

    /**
     * checks if such a user with the given email already exists.
     *
     * @param email user's email address
     * @return true if an user with the same email exists otherwise false
     */
    private static boolean userAlreadyExist(String email) throws SQLException {
        PreparedStatement statement = Statements.userExists();
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.first()) {
            //an exact user with the given email exists
            return true;
        } else {
            //no such user with the given email exists
            return false;
        }
    }

    /**
     * @param userId user's id find the user
     * @param udk    device's UDK to assign to user
     * @return always a success response
     * @throws SQLException on any sql failure
     */
    public static Response<Boolean> assignDeviceToUser(int userId, String udk) throws SQLException {
        PreparedStatement statement = Statements.assignDeviceToUser();
        statement.setString(1, udk);
        statement.setInt(2, userId);

        statement.executeUpdate();

        return Response.success(true);
    }

    /**
     * assigns user to device and viscera
     *
     * @param userId user's id to set to device
     * @param udk    the device that this request has come from to set to user
     */
    private static void assignDeviceAndUser(int userId, String udk) throws SQLException {
        assignDeviceToUser(userId, udk);
        Device.assignUserToDevice(userId, udk);
    }

    /**
     * checks if username and password matches in database
     *
     * @param username user's email address or user's unique username
     * @param password user's password
     * @return if username and password matches {@link User}, if no such user exists {@link ir.khaled.myleitner.Helper.Errors#USER_DOESNT_EXIST}
     * and if username and password don't match returns {@code false}
     */
    private static Object validateUser(String username, String password, boolean passwordHashed) throws SQLException {
        PreparedStatement statement = Statements.login();
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.first()) {
            if (!passwordHashed)
                password = Util.md5(password);
            if (password.equals(resultSet.getString("PASSWORD"))) {//password matches
                User user = new User();
                user.id = resultSet.getInt("ID");
                user.firstName = resultSet.getString("FIRST_NAME");
                user.lastName = resultSet.getString("LAST_NAME");
                user.displayName = resultSet.getString("DISPLAY_NAME");
                user.email = resultSet.getString("EMAIL_ADDRESS");
                user.picture = resultSet.getString("PICTURE");
                resultSet.close();
                return user;
            } else {//password is wrong
                resultSet.close();
                return false;
            }
        } else {
            resultSet.close();
            return Errors.USER_DOESNT_EXIST;
        }
    }


    /**
     * saves user into database <b>password must not be hashed</b>
     *
     * @param udk user's device to be set in database
     * @throws SQLException on any sql failure
     */
    private void saveUser(String udk) throws SQLException {
        PreparedStatement statement = Statements.register();
        statement.setString(1, udk);
        statement.setString(2, displayName);
        statement.setString(3, Util.md5(password));
        statement.setString(4, email);

        statement.executeUpdate();
    }
}