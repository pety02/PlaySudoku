package server.entities;

import java.io.Serializable;

public class Player implements Serializable {
    private String nickname;
    private Game[] playedGames;
    private double averageScore;

    public Player(String nickname) {
        setNickname(nickname);
        setPlayedGames(new Game[0]);
        setAverageScore();
    }

    public Player(Player copy) {
        setNickname(copy.nickname);
        setPlayedGames(copy.playedGames);
        setAverageScore();
    }

    public Player() {
        this("guest");
    }

    public String getNickname() {
        return nickname;
    }

    public Game[] getPlayedGames() {
        Game[] games = new Game[playedGames.length];
        int gameIndex = 0;
        for (Game game : playedGames) {
            games[gameIndex++] = game;
        }

        return games;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setNickname(String nickname) {
        this.nickname = (nickname != null && !nickname.equals("")) ? nickname : "guest";
    }

    public void setPlayedGames(Game[] playedGames) {
        this.playedGames = (playedGames != null) ? playedGames : new Game[0];
    }

    public void setAverageScore() {
        double avgScore = 0.0;
        int playedGamesCount = playedGames.length;
        for (Game game : playedGames) {
            avgScore += game.getCurrentScore();
        }
        this.averageScore = avgScore / playedGamesCount;
    }
}