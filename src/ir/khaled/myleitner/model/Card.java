package ir.khaled.myleitner.model;

/**
 * Created by khaled.bakhtiari on 5/2/2014.
 */
public class Card {
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
}
