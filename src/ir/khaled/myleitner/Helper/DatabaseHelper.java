package ir.khaled.myleitner.Helper;

import java.sql.*;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class DatabaseHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "myleitner";

    public Connection connection;


    public static DatabaseHelper getInstance() {
        if (instance == null || !instance.isValid())
        instance = new DatabaseHelper();
        return instance;
    }

    private DatabaseHelper() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myleitner", "root", "");
//            Statement statement = connection.createStatement();
//            statement.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
//            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public ResultSet query(String query) throws SQLException {
//        Statement statement = connection.createStatement();
//        statement.executeQuery(query);
//        return statement.getResultSet();
//    }


    private boolean isValid() {
        try {
            return connection.isValid(500);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}