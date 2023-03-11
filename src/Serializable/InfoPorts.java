package Serializable;

import java.io.Serializable;

public class InfoPorts implements Serializable {
    private int portUDP;
    private int portTCP;
    private String dirIP;

    public InfoPorts(String dirIP) {
        this.dirIP = dirIP;
    }
    public InfoPorts(int portUDP, int portTCP, String dirIP) {
        this.portUDP = portUDP;
        this.portTCP = portTCP;
        this.dirIP = dirIP;
    }
    public int getPortUDP() {
        return portUDP;
    }
    public int getPortTCP() {
        return portTCP;
    }
    public String getDirIP() {
        return dirIP;
    }
}//class