package ir.khaled.myleitner.model;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import ir.khaled.myleitner.Helper.Errors;
import ir.khaled.myleitner.Helper.Statements;
import ir.khaled.myleitner.Helper.Util;
import ir.khaled.myleitner.response.Response;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class Card {
    private static final Gson gson = new Gson();
    private static final String PARAM_CARD = "card";
    private static final String PARAM_ORDER = "order";
    private static final String PARAM_LIMIT = "limit";
    private static final String PARAM_CARD_ID = "cardId";
    private static final int DEFAULT_LIST_LIMIT = 30;

    public int id;
    public User user;
    public int leitnerId;
    public long createTime;
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


    /**
     * add card from request to database
     *
     * @param request the request from client
     * @return response to be sent to client
     * @throws Exception on any failure while deserializing json card from request to object in method {@link #getCardFromJson(String)}
     */
    public static Response<Boolean> addCard(Request request) throws Exception {
        String jsonCard = request.getParamValue(PARAM_CARD);
        if (Util.isEmpty(jsonCard))
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_CARD));

        return getCardFromJson(jsonCard).addCardToDatabase(request.getParamValue(Device.PARAM_UDK));
    }

    /**
     * returns the list of latest added cards
     *
     * @param request the request from client
     * @return list of latest cards
     */
    public static Response<ArrayList<Card>> getLastCards(Request request) throws SQLException {
        // get limit from request or if doesn't exist use default value
        int limit = Util.stringToInt(request.getParamValue(PARAM_LIMIT), DEFAULT_LIST_LIMIT);

        ArrayList<Card> lastCards = getLastCardsFromDatabase(limit);
        if (lastCards == null) {
            return Response.error(Errors.SQL_ERROR_LAST_CARDS, "couldn't get the last cards from database, request was: " + request.toString());
        } else {
            return Response.success(lastCards);
        }
    }

    /**
     * assigns a card to a leitner or if already assigned to a leitner changes the leitner
     * <ul>
     * <li>param {@link #PARAM_CARD_ID} is missing from request</li>
     * <li>param {@link ir.khaled.myleitner.model.Leitner#PARAM_LEITNER_ID} is missing from request</li>
     * </ul>
     *
     * @param request request to get {@link #PARAM_CARD_ID} and {@link ir.khaled.myleitner.model.Leitner#PARAM_LEITNER_ID} from.
     * @return a true response or if an error happens a false response with the error.
     * @throws SQLException on any sql failure
     */
    public static Response<Boolean> assignToLeitner(Request request) throws SQLException {
        int cardId = Util.stringToInt(request.getParamValue(PARAM_CARD_ID), 0);
        int leitnerId = Util.stringToInt(request.getParamValue(Leitner.PARAM_LEITNER_ID), 0);

        if (cardId < 1)
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(PARAM_CARD_ID));

        if (leitnerId < 1)
            return Response.error(Errors.MISSING_PARAM, Errors.messageMissingParam(Leitner.PARAM_LEITNER_ID));

        return assignToLeitnerDatabase(cardId, leitnerId);
    }

    /**
     * @param cardId    card to be assigned.
     * @param leitnerId id of the leitner to assign the card into.
     * @return a true response. if the operation fails exception is thrown
     * @throws SQLException on any sql failure
     */
    private static Response<Boolean> assignToLeitnerDatabase(int cardId, int leitnerId) throws SQLException {
        PreparedStatement statement = Statements.assignCardToLeitner();

        statement.setInt(1, leitnerId);
        statement.setInt(2, cardId);

        statement.executeUpdate();

        return Response.success(true);
    }

    /**
     * converts a card json to {@link ir.khaled.myleitner.model.Card}
     *
     * @param jsonCard card in json
     * @return card object
     * @throws Exception on any failure while deserializing json to object
     */
    private static Card getCardFromJson(String jsonCard) throws Exception {
        return gson.fromJson(jsonCard, Card.class);
    }

    /**
     * get the latest added cards in database
     *
     * @param limit limit of the number of cards to get
     * @return an ArrayList of cards with the size of limit
     * @throws SQLException on any sql failure
     */
    private static ArrayList<Card> getLastCardsFromDatabase(int limit) throws SQLException {
        PreparedStatement statement = Statements.lastCards();
        statement.setInt(1, limit);

        ArrayList<Card> lastCards = new ArrayList<Card>();
        ResultSet resultSet = statement.executeQuery();
        resultSet.first();
        while (resultSet.next()) {
            Card card = new Card();
            card.id = resultSet.getInt(1);
            card.title = resultSet.getString(2);
            card.front = resultSet.getString(3);
            card.back = resultSet.getString(4);
            card.createTime = resultSet.getTime(5).getTime();
            card.likeCount = resultSet.getInt(6);
            card.user = new User();
            card.user.id = resultSet.getInt(7);
            card.user.displayName = resultSet.getString(8);
            card.user.picture = resultSet.getString(9);
            lastCards.add(card);
        }
        resultSet.close();
        return lastCards;
    }

    /**
     * @param udk the devices that this card has been added from
     * @return a new instance of {@link ir.khaled.myleitner.response.Response} with result of true
     * @throws Exception on any failure
     */
    private Response<Boolean> addCardToDatabase(String udk) throws Exception {
        PreparedStatement statement = Statements.addCard();

        setDevice(statement, 1, udk);

        int userId = User.getUserId(udk);
        //if user exists set card's user otherwise set to null
        if (userId == User.NO_USER)
            statement.setNull(2, Types.INTEGER);
        else setUser(statement, 2, userId);

        statement.setInt(3, leitnerId);
        statement.setString(4, title);
        statement.setString(5, front);
        statement.setString(6, back);

        //execute insert sql to database by statement
        statement.executeUpdate();

        return Response.success(true);
    }

    /**
     * set the device to statement
     *
     * @param position position in statement
     * @param udk      device's udk
     * @throws SQLException on any sql failure
     */
    private Card setDevice(PreparedStatement statement, int position, String udk) throws SQLException {
        statement.setString(position, udk);
        return this;
    }

    /**
     * set user to the statement
     *
     * @param position position in statement
     * @param userId   user's id
     * @throws SQLException on any sql failure
     */
    private Card setUser(PreparedStatement statement, int position, int userId) throws SQLException {
        statement.setInt(position, userId);
        return this;
    }

}
