package ir.khaled.myleitner.log;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import ir.khaled.myleitner.Helper.Statements;
import ir.khaled.myleitner.model.User;

/**
 * Created by khaled on 9/6/2014.
 */
public class RequestLog {
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
        PreparedStatement statement = Statements.addLog();
        statement.setInt(1, User.getUserId(udk));
        statement.setString(2, udk);
        statement.setString(3, action);
        statement.setString(4, target);
        statement.setInt(5, takenTime);

        statement.executeUpdate();
    }


}
