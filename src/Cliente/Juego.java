package Cliente;

import Serializable.InfoPorts;
import Serializable.Jugador;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.*;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Juego extends JFrame {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "Monstruitos";
    public static JButton btnTopos[] = new JButton[16];
    public static boolean tablero[] = new boolean[16];
    private JLabel lblScore;
    private JLabel lblTimeLeft;
    private JButton btnSalir;
    private ImageIcon topoInImg = new ImageIcon(getClass().getResource("background.jpeg"));
    private ImageIcon topoOutImg = new ImageIcon(getClass().getResource("monstruito.jpeg"));
    private static Icon topoInImgRedo;
    private static Icon topoOutImgRedo;
    public static int score;
    private final int duracion = 30;
    private final int topoWidth = 132;
    private final int topoHeight = 132;
    private ScheduledExecutorService executor;
    private Jugador player;
    private InfoPorts info;
    private Socket socketTCP;
    private ObjectOutputStream out;
    private boolean juegoIniciado = true;
    private Monstruito topoHilo;
    public Juego(Jugador player,InfoPorts info) throws IOException, JMSException {
        this.player = player;
        this.info = info;
        score = 0;

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(subject);
        MessageConsumer messageConsumer = session.createConsumer(destination);

        init();
        initConnection();
        iniciaJuego();

        topoHilo = new Monstruito();
        topoHilo.start();
    }
    public void init() {
        setTitle("Pégale al monstruo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 100, 608, 720);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(0, 0, 0));
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel. setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(35, 105, 528, 529);
        panel.setLayout(null);
        contentPanel.add(panel);

        lblScore = new JLabel("Score: 0");
        lblScore.setForeground(new Color(135, 206, 250));
        lblScore.setHorizontalAlignment(SwingConstants.TRAILING);
        lblScore.setFont(new Font("Cambria", Font.BOLD, 14));
        lblScore.setBounds(423, 54, 144, 33);
        contentPanel.add(lblScore);

        lblTimeLeft = new JLabel(player.getPlayerId());
        lblTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimeLeft.setForeground(new Color(240, 128, 128));
        lblTimeLeft.setFont(new Font("Cambria", Font.BOLD, 24));
        lblTimeLeft.setBounds(232, 54, 144, 33);
        contentPanel.add(lblTimeLeft);

        topoInImgRedo = resizeIcon(topoInImg, topoWidth, topoHeight);
        topoOutImgRedo = resizeIcon(topoOutImg, topoWidth, topoHeight);

        for (int i = 0, x = 0, y = 396; i < 16; i++) {
            btnTopos[i] = new JButton();
            btnTopos[i].setName(String.valueOf(i));
            btnTopos[i].setBounds(x, y, topoWidth, topoHeight);
            panel.add(btnTopos[i]);
            btnTopos[i].setIcon(topoInImgRedo);
            x = (x + topoWidth) % 528;
            y = x == 0 ? y - topoHeight : y;
            tablero[i] = false;
        }
        setContentPane(contentPanel);
    }
    private void initConnection() {
        try {
            String serverIP = this.info.getDirIP();
            int serverPort = this.info.getPortTCP();
            socketTCP = new Socket("localhost",49152);
            out = new ObjectOutputStream(socketTCP.getOutputStream());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    public static int creaTopo(int topoID) {
        tablero[topoID] = true;
        btnTopos[topoID].setIcon(topoOutImgRedo);
        return topoID;
    }
    private void clickTopo(int topoID) {
        if (juegoIniciado) {
            if (tablero[topoID]) {
                score++;
                this.player.setPlayerScore(score);
                btnTopos[topoID].setIcon(topoInImgRedo);
                tablero[topoID] = false;
                lblScore.setText(String.valueOf(score));
                try {
                    out.writeObject(this.player);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    private void iniciaJuego() {
        for (JButton topo : btnTopos) {
            topo.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO Auto-generated method stub
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    JButton btn = (JButton) e.getSource();
                    int topoID = Integer.parseInt(btn.getName());
                    clickTopo(topoID);
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub
                }
            });
        }
    }
    public static void limpiaTopo(int topoID) {
        tablero[topoID] = false;
        btnTopos[topoID].setIcon(topoInImgRedo);
    }

}