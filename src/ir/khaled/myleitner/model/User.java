package ir.khaled.myleitner.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ir.khaled.myleitner.Helper.DatabaseHelper;
import ir.khaled.myleitner.Helper.ErrorHelper;
import ir.khaled.myleitner.response.Response;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class User {
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final int NO_USER = -1;
    private static PreparedStatement statementLogin;
    private static PreparedStatement statementRegister;
    /**
     * Statement to get serId from UDK
     */
    private static PreparedStatement statementUserId;
    private static PreparedStatement statementAssignDevice;

    public int id;
    public String firstName;
    public String lastName;
    public String username;
    public String displayName;
    public String email;
    public String picture;
    public Biography biography;
    public Device device;

    public static int getUserId(String udk) throws SQLException {
        PreparedStatement statement = getStatementUserId();
        statement.setInt(1, Device.getDeviceId(udk));

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.first()) {
            return resultSet.getInt("USER_ID");//column USER_ID from table user_device
        } else {
            return NO_USER;
        }
    }

    public static Response<User> loginUser(Request request) throws SQLException {
        String username = request.getParamValue(PARAM_USERNAME);
        String password = request.getParamValue(PARAM_PASSWORD);

        Object object = validateUser(username, password);
        if (object instanceof User) {//user successfully logged in
            User loggedInUser = (User) object;
            assignDeviceAndUser(loggedInUser.id, request.getUDK());
            return Response.success(loggedInUser);
        } else if (object instanceof Integer) {//no user with the given username or email exists
            return Response.error(ErrorHelper.USER_DOESNT_EXIST, "no user with the given username or email exists");
        } else {//username and password didn't match
            return Response.error(ErrorHelper.WRONG_USERNAME_PASSWORD, "username or password is wrong");
        }
    }

    /**
     * @param userId user's id find the user
     * @param udk device's UDK to assign to user
     * @return always a success response
     * @throws SQLException on any sql failure
     */
    public static Response<Boolean> assignDeviceToUser(int userId, String udk) throws SQLException {
        PreparedStatement statement = getStatementAssignDevice();
        statement.setString(1, udk);
        statement.setInt(2, userId);

        statement.executeUpdate();

        return Response.success(true);
    }

    /**
     * assigns user to device and viscera
     * @param userId user's id to set to device
     * @param udk the device that this request has come from to set to user
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
     * @return if username and password matches {@link User}, if no such user exists {@link ir.khaled.myleitner.Helper.ErrorHelper#USER_DOESNT_EXIST}
     * and if username and password don't match returns {@code false}
     */
    private static Object validateUser(String username, String password) throws SQLException {
        PreparedStatement statement = getStatementLogin();
        statement.setString(1, username);
        statement.setString(2, username);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.first()) {
            if (password.equals(resultSet.getString("PASSWORD"))) {//password matches
                User user = new User();
                user.id = resultSet.getInt("ID");
                user.firstName = resultSet.getString("FIRST_NAME");
                user.lastName = resultSet.getString("LAST_NAME");
                user.username = resultSet.getString("USERNAME");
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
            return ErrorHelper.USER_DOESNT_EXIST;
        }
    }

    private static synchronized PreparedStatement getStatementLogin() throws SQLException {
        if (statementLogin == null) {
            statementLogin = DatabaseHelper.getConnection().prepareStatement("SELECT * FROM USER WHERE EMAIL_ADDRESS = ? OR USERNAME = ?");
        }
        return statementLogin;
    }

    private static synchronized PreparedStatement getStatementUserId() throws SQLException {
        if (statementUserId == null) {
            statementUserId = DatabaseHelper.getConnection().prepareStatement("SELECT USER_ID FROM DEVICE WHERE ID = ?");
        }
        return statementUserId;
    }

    private static synchronized PreparedStatement getStatementAssignDevice() throws SQLException {
        if (statementAssignDevice == null) {
            statementAssignDevice = DatabaseHelper.getConnection().prepareStatement("UPDATE USER SET DEVICE_UDK = ? WHERE ID = ?");
        }
        return statementAssignDevice;
    }

    private static synchronized PreparedStatement getStatementRegister() throws SQLException {
        if (statementRegister == null) {
            statementRegister = DatabaseHelper.getConnection().prepareStatement()
        }
    }
}