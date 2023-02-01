package server.entities;

import java.io.Serializable;

/**
 * Клас, който описва играча.
 */
public class Player implements Serializable {
    private String nickname;
    private Game[] playedGames;
    private double averageScore;

    /**
     * Конструктор с параметри.
     * @param nickname - псевдонима на играча в играта.
     * @param playedGames - масив от изиграните игри на играча.
     */
    public Player(String nickname, Game[] playedGames) {
        setNickname(nickname);
        setPlayedGames(playedGames);
        setAverageScore();
    }

    /**
     * Конструктор с параметър.
     * @param nickname - псевдонима на играча в играта.
     */
    public Player(String nickname) {
        setNickname(nickname);
        setPlayedGames(new Game[0]);
        setAverageScore();
    }

    /**
     * Конструктор без параметри.
     */
    public Player() {
        setNickname("guest");
        setPlayedGames(new Game[0]);
        setAverageScore();
    }

    /**
     * Гетър за никнейма.
     * @return - стойността на никнейма.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Гетър за изиграните игри от играча.
     * @return масив от изиграните игри от играча.
     */
    public Game[] getPlayedGames() {
        return playedGames;
    }

    /**
     * Гетър за средния резултат на играча. Определя се въз основа
     * на резултатите му във всички изиграни игри като средно
     * аритметично на резултатите им.
     * @return средно аритметично на резултатите на всички изиграни игри.
     */
    public double getAverageScore() {
        return averageScore;
    }

    /**
     * Сетър за никнейма.
     * @param nickname - никнейм.
     */
    public void setNickname(String nickname) {
        this.nickname = (nickname != null && !nickname.equals("")) ? nickname : "guest";
    }

    /**
     * Сетър за изиграните игри.
     * @param playedGames - изиграни игри.
     */
    public void setPlayedGames(Game[] playedGames) {
        this.playedGames = (playedGames != null) ? playedGames : new Game[0];
    }

    /**
     * Сетър за средния резултат на играча - средно аритметично на резултатите
     * от всички изиграни игри.
     */
    public void setAverageScore() {
        double avgScore = 0.0;
        int playedGamesCount = playedGames.length;
        for (Game game : playedGames) {
            avgScore += game.getCurrentScore();
        }
        this.averageScore = avgScore / playedGamesCount;
    }
}