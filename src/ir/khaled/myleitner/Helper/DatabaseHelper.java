package ir.khaled.myleitner.Helper;

import java.sql.*;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class DatabaseHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "myleitner";

    private Connection connection;


    public static DatabaseHelper getInstance() {
        if (instance == null || !instance.isValid())
        instance = new DatabaseHelper();
        return instance;
    }

    public static Connection getConnection() {
        return getInstance().connection;
    }

    private DatabaseHelper() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myleitner", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid() {
        try {
            return connection.isValid(500);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}