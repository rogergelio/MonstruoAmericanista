package Servidor;

import Serializable.Jugador;
import java.net.*;
import java.io.*;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TCPServer extends Thread{
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    // default broker URL is : tcp://localhost:61616"
    private static String subject = "Monstruitos"; // Topic Name. You can create any/many topic names as per your requirement.
    public TCPServer() throws JMSException {
        super();
    }//builder
    private Partida game = new Partida();
    public Partida getGame() {
        return game;
    }
    @Override
    public void run(){
        try {
            System.out.println("entra a tcpServer");
            int serverPort = 49152;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("antes accept");
                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                System.out.println("despues accept");
                Connection2 c = new Connection2(clientSocket,game);
                c.start();
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}//TCP Server
class Connection2 extends Thread {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String subject = "Las_mopus_de_roger"; // Topic Name. You can create any/many topic names as per your requirement.


    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private Partida game;
    private Jugador player;

    public Connection2(Socket aClientSocket, Partida game) {
        try {
            clientSocket = aClientSocket;
            System.out.println("Entró al try de connection2");
            MessageProducer messageProducer;
            TextMessage textMessage;
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(subject);

            messageProducer = session.createProducer(destination);
            textMessage = session.createTextMessage();

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            this.game = game;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("Entra al Run del TCPServer");
        try{
            player = (Jugador) in.readObject(); // recibimos el objeto de jugador que envia el cliente
            if(game.isNewPlayer(player)){
                game.addPlayer(player);
            }
            Jugador player;
            System.out.println("Entramos a esto");
            while(true){
                player = (Jugador) in.readObject();
                System.out.println("jugador le pego al monstruo: "+ player.getPlayerId() +" score: "+player.getPlayerScore());
                game.updateScore(player);
            }//while
        }//try
        catch(Exception e){
            e.printStackTrace();
        }
    }//run
}//Connection
