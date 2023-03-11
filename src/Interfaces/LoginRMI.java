package Interfaces;

import Serializable.InfoPorts;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginRMI extends Remote{
    public InfoPorts getInfo(String player) throws RemoteException;
}
