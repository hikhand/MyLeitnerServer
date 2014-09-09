package ir.khaled.myleitner.log;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import ir.khaled.myleitner.Helper.DatabaseHelper;
import ir.khaled.myleitner.model.User;

/**
 * Created by khaled on 9/6/2014.
 */
public class WebserviceLog {
    private static PreparedStatement statementAddLog;
//    public int id;
//    public String action;
//    public String target;
//    public String udk;
//    public int takenTime;
//
//    public WebserviceLog() {
//        this.udk = udk;
//        this.action = action;
//        this.target = target;
//        this.takenTime = takenTime;
//    }

    /**
     * saves the log into database.
     *
     * @param udk       device's udk
     * @param action    log's action
     * @param target    description of the log
     * @param takenTime the time it took to response to client
     * @throws SQLException on any sql failure
     */
    public static void saveLog(String udk, String action, String target, int takenTime) throws SQLException {
        PreparedStatement statement = getStatementLogin();
        statement.setInt(1, User.getUserId(udk));
        statement.setString(2, udk);
        statement.setString(3, action);
        statement.setString(4, target);
        statement.setInt(5, takenTime);

        statement.executeUpdate();
    }

    private static synchronized PreparedStatement getStatementLogin() throws SQLException {
        if (statementAddLog == null) {
            statementAddLog = DatabaseHelper.getConnection().prepareStatement("INSERT INTO LOG (USER_ID, UDK, ACTION, TARGET, TAKEN_TIME) VALUES (?, ?, ?, ?, ?)");
        }
        return statementAddLog;
    }
}
