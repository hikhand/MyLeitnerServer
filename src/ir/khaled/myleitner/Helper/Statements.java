package ir.khaled.myleitner.Helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by khaled on 9/9/2014.
 */
public class Statements {
    private static PreparedStatement createLeitner;
    private static PreparedStatement leitnerExists;
    private static PreparedStatement login;
    private static PreparedStatement register;
    private static PreparedStatement userExists;
    private static PreparedStatement getUserId;
    private static PreparedStatement assignDevice;
    private static PreparedStatement isUserLoggedIn;
    private static PreparedStatement addCard;
    private static PreparedStatement lastCards;
    private static PreparedStatement addLog;
    private static PreparedStatement checkDevice;
    private static PreparedStatement registerDevice;
    private static PreparedStatement getDeviceId;
    private static PreparedStatement assignUserToDevice;
    private static PreparedStatement assignCardToLeitner;

    public static synchronized PreparedStatement login() throws SQLException {
        if (login == null)
            login = DatabaseHelper.getConnection().prepareStatement("SELECT * FROM USER WHERE EMAIL_ADDRESS = ?");
        return login;
    }

    public static synchronized PreparedStatement getUserId() throws SQLException {
        if (getUserId == null)
            getUserId = DatabaseHelper.getConnection().prepareStatement("SELECT USER_ID FROM DEVICE WHERE ID = ?");
        return getUserId;
    }

    public static synchronized PreparedStatement assignDeviceToUser() throws SQLException {
        if (assignDevice == null)
            assignDevice = DatabaseHelper.getConnection().prepareStatement("UPDATE USER SET DEVICE_UDK = ? WHERE ID = ?");
        return assignDevice;
    }

    public static synchronized PreparedStatement register() throws SQLException {
        if (register == null)
            register = DatabaseHelper.getConnection().prepareStatement("INSERT INTO USER (DEVICE_UDK, DISPLAY_NAME, PASSWORD, EMAIL_ADDRESS) VALUES (?, ?, ?, ?)");
        return register;
    }

    public static synchronized PreparedStatement userExists() throws SQLException {
        if (userExists == null)
            userExists = DatabaseHelper.getConnection().prepareStatement("SELECT EMAIL_ADDRESS FROM USER WHERE EMAIL_ADDRESS = ?");
        return userExists;
    }

    public static synchronized PreparedStatement createLeitner() throws SQLException {
        if (createLeitner == null)
            createLeitner = DatabaseHelper.getConnection().prepareStatement("INSERT INTO LEITNER (USER_ID, NAME) VALUES (?, ?)");
        return createLeitner;
    }

    public static synchronized PreparedStatement leitnerExists() throws SQLException {
        if (leitnerExists == null)
            leitnerExists = DatabaseHelper.getConnection().prepareStatement("SELECT ID FROM LEITNER WHERE USER_ID = ? AND NAME = ?");
        return leitnerExists;
    }

    public static synchronized PreparedStatement loginCheck() throws SQLException {
        if (isUserLoggedIn == null)
            isUserLoggedIn = DatabaseHelper.getConnection().prepareStatement("SELECT USER_ID FROM DEVICE WHERE UDK = ?");
        return isUserLoggedIn;
    }

    public static synchronized PreparedStatement addCard() throws SQLException {
        if (addCard == null)
            addCard = DatabaseHelper.getConnection().prepareStatement("INSERT INTO CARD (DEVICE_UDK, USER_ID, LEITNER_ID, TITLE, FRONT, BACK) VALUES (?, ?, ?, ?, ?)");
        return addCard;
    }

    public static synchronized PreparedStatement lastCards() throws SQLException {
        if (lastCards == null)
            lastCards = DatabaseHelper.getConnection().prepareStatement(
                    "SELECT CARD.ID, CARD.TITLE, CARD.FRONT, CARD.BACK, CARD.CREATE_TIME, CARD.LIKE_COUNT, " +
                            "USER.ID, USER.DISPLAY_NAME, USER.PICTURE " +
                            "FROM CARD INNER JOIN USER ON USER.ID = CARD.USER_ID " +
                            "ORDER BY CARD.CREATE_TIME DESC LIMIT ?");
        return lastCards;
    }

    public static synchronized PreparedStatement addLog() throws SQLException {
        if (addLog == null)
            addLog = DatabaseHelper.getConnection().prepareStatement("INSERT INTO LOG (USER_ID, UDK, ACTION, TARGET, TAKEN_TIME) VALUES (?, ?, ?, ?, ?)");
        return addLog;
    }

    public static synchronized PreparedStatement registerDevice() throws SQLException {
        if (registerDevice == null)
            registerDevice = DatabaseHelper.getConnection().prepareStatement("INSERT INTO DEVICE (UDK, DENSITY_DPI, SIZE_INCHES, HEIGHT, DENSITY, WIDTH, XDPI, YDPI, STORAGE_EXTERNAL, STORAGE_EXTERNAL_FREE, STORAGE_INTERNAL, RAM_SIZE, CPU_ABI, CPU_ABI2, MAX_FREQUENCY, CORES, ANDROID_ID, BLUETOOTH_ADDRESS, BOARD, BRAND, DEVICE_NAME, DISPLAY_NAME, LABEL, IMEI, MANUFACTURE, MODEL, PRODUCT, WLAN_ADDRESS, SDK_VERSION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        return registerDevice;
    }

    public static synchronized PreparedStatement getDeviceId() throws SQLException {
        if (getDeviceId == null)
            getDeviceId = DatabaseHelper.getConnection().prepareStatement("SELECT ID FROM device WHERE UDK = ?");
        return getDeviceId;
    }

    public static synchronized PreparedStatement checkDevice() throws SQLException {
        if (checkDevice == null)
            checkDevice = DatabaseHelper.getConnection().prepareStatement("SELECT UDK FROM DEVICE WHERE UDK=?");
        return checkDevice;
    }

    public static synchronized PreparedStatement assignUserToDevice() throws SQLException {
        if (assignUserToDevice == null)
            assignUserToDevice = DatabaseHelper.getConnection().prepareStatement("UPDATE DEVICE SET USER_ID = ? WHERE UDK = ?");
        return assignUserToDevice;
    }

    public static synchronized PreparedStatement assignCardToLeitner() throws SQLException {
        if (assignCardToLeitner == null)
            assignCardToLeitner = DatabaseHelper.getConnection().prepareStatement("UPDATE CARD SET LEITNER_ID = ? WHERE ID = ?");
        return assignCardToLeitner;
    }
}
