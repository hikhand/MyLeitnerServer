package ir.khaled.myleitner.model;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import ir.khaled.myleitner.Helper.DatabaseHelper;
import ir.khaled.myleitner.Helper.ErrorHelper;
import ir.khaled.myleitner.Helper.Util;
import ir.khaled.myleitner.response.Response;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class Card {
    private static final Gson gson = new Gson();
    private static final String PARAM_CARD = "card";
    private static PreparedStatement statementAddCard;

    public int id;
    public int userId;
    public int leitnerId;
    public int createTime;
    public int checkTime;
    public int likeCount;
    public int boxIndex;
    public int deckIndex;
    public int countCorrect;
    public int countIncorrect;
    public String title;
    public String front;
    public String back;
    public Tag[] tags;
    public Category category;
    public Comment[] comments;
    public Example[] examples;

    private PreparedStatement statement;

    public static Response<Boolean> addCard(Request request) throws Exception {
        String jsonCard = request.getParamValue(PARAM_CARD);
        if (Util.isEmpty(jsonCard))
            return new Response<Boolean>(ErrorHelper.INVALID_PARAM, "Param '" + PARAM_CARD + "' is empty");

        return getCard(jsonCard).addCardToDatabase(request.getParamValue(Device.PARAM_UDK));
    }

    private static Card getCard(String jsonCard) throws Exception {
        return gson.fromJson(jsonCard, Card.class);
    }

    /**
     * set the device to statement
     * @param position position in statement
     * @param udk device's udk
     * @throws SQLException on any sql failure
     */
    private Card setDevice(int position, String udk) throws SQLException {
        statement.setString(position, udk);
        return this;
    }

    /**
     * set user to the statement
     * @param position position in statement
     * @param userId user's id
     * @throws SQLException on any sql failure
     */
    private Card setUser(int position, int userId) throws SQLException {
        statement.setInt(position, userId);
        return this;
    }

    /**
     * set card's title to statement from {@link #title}
     * @param position position in statement
     * @throws SQLException on any sql failure
     */
    private Card setTitle(int position) throws SQLException {
        statement.setString(position, title);
        return this;
    }

    /**
     * set card's front to statement from {@link #front}
     * @param position position in statement
     * @throws SQLException on any sql failure
     */
    private Card setFront(int position) throws SQLException {
        statement.setString(position, front);
        return this;
    }

    /**
     * set card's back to statement from {@link #back}
     * @param position position in statement
     * @throws SQLException on any sql failure
     */
    private Card setBack(int position) throws SQLException {
        statement.setString(position, back);
        return this;
    }

    /**
     *
     *
     * @param udk the devices that this card has been added from
     * @return a new instance of {@link ir.khaled.myleitner.response.Response} with result of true
     * @throws Exception on any failure
     */
    private Response<Boolean> addCardToDatabase(String udk) throws Exception {
        statement = getStatementAddCard();

        setDevice(1, udk);

        int userId = User.getUserId(udk);
        //if user exists set card's user otherwise set to null
        if (userId == User.NO_USER)
            statement.setNull(2, Types.INTEGER);
        else setUser(2, userId);

        setTitle(3);
        setFront(4);
        setBack(5);

        //execute insert sql to database by statement
        statement.executeUpdate();

        return new Response<Boolean>(true);
    }

    /**
     * @return singleton instance of PreparedStatement to addCard to database
     * @throws SQLException on any sql failure
     */
    private PreparedStatement getStatementAddCard() throws SQLException {
        if (statementAddCard == null) {
            statementAddCard = DatabaseHelper.getConnection().prepareStatement("INSERT INTO card (DEVICE_UDK, USER_ID, TITLE, FRONT, BACK) VALUES (?, ?, ?, ?, ?)");
        }
        return statementAddCard;
    }
}
