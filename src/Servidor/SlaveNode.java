package Servidor;

import Interfaces.LoginRMI;
import Serializable.InfoPorts;
import Serializable.Jugador;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class SlaveNode implements LoginRMI {
    private String ip = "224.0.0.1";
    private int portTCP = 49152;
    private int portUDP = 49155;
    private ArrayList<Jugador> jugadores;
    public SlaveNode() throws RemoteException {
        super();
    }//Constructor
    public void deploy() {
        try {
            SlaveNode engine = new SlaveNode();
            LoginRMI stub = (LoginRMI) UnicastRemoteObject.exportObject(engine,0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("LoginRMI",stub);
            System.out.println("Servicio RMI desplegado");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception");
            e.printStackTrace();
        }//catch
    }//deploy
    @Override
    public InfoPorts getInfo(String player) throws RemoteException{
        InfoPorts info = new InfoPorts(ip);
        return info;
    }
}//class
