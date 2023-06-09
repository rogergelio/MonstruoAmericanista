package Servidor;

import Serializable.Jugador;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Partida {
    private int maxScore;
    private InetAddress group;
    private MulticastSocket socket;
    public ArrayList<Jugador> players;
    private MulticastSocket msocket = null;
    Random rand = new Random();
    private static String url = "tcp://10.10.23.244:61616";
    private static String subject = "Monstruito_Americanista"; // Topic Name. You can create any/many topic names as per your requirement.
    public Partida() throws JMSException {
    }
    public int getMaxScore() {
        return maxScore;
    }
    public void initialize(){
        try {
            this.group = Inet4Address.getByName("224.0.0.1"); //destination multicast group
            this.socket = new MulticastSocket(49152);
            this.socket.joinGroup(group);
            this.maxScore = 5;
            Jugador defaultP = new Jugador("Server",1);
            players = new ArrayList<Jugador>();
            players.add(defaultP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void GameOver() throws JMSException {
        String myMessage = "_" + getPlayerMaxScore().getPlayerId();
        System.out.println("Jugador Ganador: " + myMessage);
        MessageProducer messageProducer;
        TextMessage textMessage;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(subject);
        messageProducer = session.createProducer(destination);
        textMessage = session.createTextMessage();

        textMessage.setText(myMessage);
        System.out.println("Game over message: " + textMessage.getText());
        messageProducer.send(textMessage);
    }
    public Jugador getPlayerMaxScore(){
        Jugador maxPlayer = null, currentPlayer = null;
        int maxScore = -1, currentScore;
        for (int i = 0; i < players.size(); i++) {
            currentPlayer = players.get(i);
            currentScore = currentPlayer.getPlayerScore();
            if (currentScore > maxScore) {
                maxScore = currentScore;
                maxPlayer = currentPlayer;
            }
        }
        return maxPlayer;
    }
    public void sendMonster() throws JMSException {
        MessageProducer messageProducer;
        TextMessage textMessage;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(subject);
        messageProducer = session.createProducer(destination);
        textMessage = session.createTextMessage();

        int hole = rand.nextInt(16);
        String myMessage = Integer.toString(hole);
        System.out.println("Casilla enviada " + hole);

        textMessage.setText(myMessage);
        messageProducer.send(textMessage);

    }//sendMonster
    public boolean isNewPlayer(Jugador newPlayer){
        System.out.println("Entró a isnewplayer");
        boolean resp;
        resp = true;
        Jugador player;
        String newId = newPlayer.getPlayerId();
        for (int i = 1; i < players.size(); i++) {
            player = players.get(i);
            System.out.println("Imprimió: "+player.getPlayerId());
            System.out.println("vez " + i+" en el for");
            if (player.getPlayerId().equals(newId)) {
                System.out.println("player id:" + player.getPlayerId());
                System.out.println("new player id:" + newId);
                resp = false;
            }
        }
        return resp;
    }

    /*public void mantenPuntaje(Jugador newPlayer){
        String newId=newPlayer.getPlayerId();
        if(!isNewPlayer(newPlayer)){
            for (int i = 1; i < players.size(); i++) {
                Jugador player = players.get(i);
                if (player.getPlayerId().equals(newId)) {
                    newPlayer.setPlayerScore(player.getPlayerScore());
                }
            }
        }

    }*/

    public void updateScore(Jugador player1){
        System.out.println("Entó a update");
        Jugador player2;
        for (int i = 0; i < players.size(); i++) {
            player2 = players.get(i);
            String player2ID = player2.getPlayerId();
            if (player1.getPlayerId().equals(player2ID)) {
                System.out.println("Entró al if de update");
                player2.setPlayerScore(player2.getPlayerScore()+1);
                player1.setPlayerScore(player2.getPlayerScore());
                System.out.println("Puntaje" + player2.getPlayerScore());
                System.out.println("Jugador 1");
                System.out.println(player1.toString());
                System.out.println("Jugador 2");
                System.out.println(player2.toString());
                players.set(i,player2);
            }
        }
    }
    public void resetScores(){
        for(int i = 0; i < players.size(); i++){
            Jugador player = players.get(i);
            player.setPlayerScore(0);
            players.set(i,player);
        }
    }
    public void addPlayer(Jugador newPlayer){
        this.players.add(newPlayer);
    }

}//Partida
