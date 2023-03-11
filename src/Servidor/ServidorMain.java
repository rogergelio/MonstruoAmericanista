package Servidor;

import Serializable.Jugador;
import javax.jms.JMSException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorMain {
    public static void main(String[] args) throws IOException, InterruptedException, JMSException {
        System.setProperty("java.security.policy", "src/Server/server.policy");
        //Desplegar servicio RMI
        try {
            String serverAddress = "localhost";
            System.setProperty("java.rmi.server.hostname", serverAddress);
            // start the rmi registry
            LocateRegistry.createRegistry(1099);   /// default port
            SlaveNode slave = new SlaveNode();
            slave.deploy();
        } catch (Exception e) {
            System.err.println("ComputeEngine exception");
            e.printStackTrace();
        }//catch
        //Instanciar servidor TCP y empezar el jeugo
        TCPServer servTCP = new TCPServer();
        System.out.println(servTCP);
        servTCP.start();
        Partida game = servTCP.getGame();
        //Instanciar grupo multicast
        game.initialize();
        Jugador maxPlayer;
        int maxScore = game.getMaxScore();
        //mientras nadie haya ganado, enviamos monstruos por multicast
        while(true){
            game.sendMonster();
            Thread.sleep(1000);
            //Checar si alguien ya gano
            maxPlayer = game.getPlayerMaxScore();
            System.out.println("Max player: " + maxPlayer.getPlayerScore());
            if(maxPlayer.getPlayerScore()==maxScore) {
                System.out.println("If game over");
                game.GameOver();
                game.resetScores();
            }
        }
        //Cuando alguien ya gano enviamos al ganador
    }//main
}//class
