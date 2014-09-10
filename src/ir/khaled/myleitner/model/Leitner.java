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
public class Leitner {
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_LEITNER_ID = "leitnerId";
    public int id;
    public int userId;
    public int createTime;
    public int likeCount;

    /**
     * create a new leitner for the user.<br/>
     * possible erorrs are:
     * <ul>
     * <li>user doesn't exist</li>//TODO add check for user exists or not
     * <li>user is not login</li>
     * <li>param {@link #PARAM_USER_ID} is missing from request</li>
     * <li>param {@link #PARAM_NAME} is missing from request</li>
     * <li>user already has a leitner with this name</li>
     * </ul>
     *
     * @param request the request to get {@link #PARAM_USER_ID} and {@link #PARAM_NAME} from.
     * @return a boolean response if the leitner is successfully created. if can't create leitner the error response is returned.
     * @throws Exception on any failure
     */
    public static Response<Boolean> create(Request request) throws Exception {
        int userId = Util.stringToInt(request.getParamValue(PARAM_USER_ID), 0);
        String name = request.getParamValue(PARAM_NAME);

        if (userId < 1)
            return Response.error(Errors.MISSING_PARAM, "param '" + PARAM_USER_ID + "' is missing.");

        if (Util.isEmpty(name))
            return Response.error(Errors.MISSING_PARAM, "param '" + PARAM_NAME + "' is missing.");

        if (!User.isUserLoggedIn(userId, request.getUDK()))
            return Response.error(Errors.USER_IS_NOT_LOGGED_IN, "user is not logged in");

        if (leitnerExists(userId, name))
            return Response.error(Errors.LEITNER_ALREADY_EXISTS, "a leitner with the name of '" + name + "'" + "for this user already exists");

        return addToDatabase(userId, name);
    }

    /**
     * checks to see whether a existing leitner for the user with this name exist or not
     *
     * @param userId id of the user that this leitner belongs to
     * @param name   name of the leitner to be checked
     * @return true if a leitner with this name for user exists. if doesn't exists returns false
     * @throws SQLException on any sql failure
     */
    public static boolean leitnerExists(int userId, String name) throws SQLException {
        PreparedStatement statement = Statements.leitnerExists();

        statement.setInt(1, userId);
        statement.setString(2, name);

        ResultSet resultSet = statement.executeQuery();

        return resultSet.next();
    }

    /**
     * adds a new row to leitner table with for the requested user.
     *
     * @param userId id of the user that this leitner belongs to
     * @param name   a name for the leitner
     * @return response of boolean type with success data.
     * @throws SQLException on any sql failure
     */
    public static Response<Boolean> addToDatabase(int userId, String name) throws SQLException {
        PreparedStatement statement = Statements.createLeitner();
        statement.setInt(1, userId);
        statement.setString(2, name);

        statement.executeUpdate();

        return Response.success(true);
    }
}
