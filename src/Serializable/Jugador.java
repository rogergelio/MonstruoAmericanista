package Serializable;

import java.io.Serializable;

public class Jugador implements Serializable {
    private String playerId;
    private int playerScore;
    public Jugador(String playerId, int playerScore) {
        this.playerId = playerId;
        this.playerScore = playerScore;
    }
    public String getPlayerId() {
        return playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    public int getPlayerScore() {
        return playerScore;
    }
    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "playerId='" + playerId + '\'' +
                ", playerScore=" + playerScore +
                '}';
    }
}