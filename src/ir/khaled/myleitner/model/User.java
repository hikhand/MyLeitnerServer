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

    public int id;
    public String firstName;
    public String lastName;
    public String username;
    public String displayName;
    public String email;
    public String picture;
    public Biography biography;
    public Device device;

    public static int getUserId(String udk) {
        return NO_USER;
    }

    public static Response<User> loginUser(Request request) throws SQLException {
        //TODO after login assign this device to user
        String username = request.getParamValue(PARAM_USERNAME);
        String password = request.getParamValue(PARAM_PASSWORD);

        Object object = validateUser(username, password);
        if (object instanceof User) {//user successfully logged in
            User loggedInUser = (User) object;
            return Response.success(loggedInUser);
        } else if (object instanceof Integer) {//no user with the given username or email exists
            return Response.error(ErrorHelper.USER_DOESNT_EXIST, "no user with the given username or email exists");
        } else {//username and password didn't match
            return Response.error(ErrorHelper.WRONG_USERNAME_PASSWORD, "username or password is wrong");
        }
    }

    /**
     * checks if username and password matches in database
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
//            statementLogin = DatabaseHelper.getConnection().prepareStatement("SELECT * FROM USER WHERE PASSWORD = ? AND (EMAIL_ADDRESS = ? OR USERNAME = ?)");
            statementLogin = DatabaseHelper.getConnection().prepareStatement("SELECT * FROM USER WHERE EMAIL_ADDRESS = ? OR USERNAME = ?");
        }
        return statementLogin;
    }
}