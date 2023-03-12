package Cliente;
import Serializable.InfoPorts;
import Serializable.Jugador;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Juego extends JFrame {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "Monstruito_Americanista";
    public static JButton btnMonstruos[] = new JButton[16];
    public static boolean tablero[] = new boolean[16];
    private JLabel lblScore;
    private JLabel lblTimeLeft;
    private ImageIcon monstruoInImg = new ImageIcon(getClass().getResource("background.jpeg"));
    private ImageIcon monstruoOutImg = new ImageIcon(getClass().getResource("monstruito.jpeg"));
    private static Icon monstruoInImgRedo;
    private static Icon monstruoOutImgRedo;
    public static int score;
    private final int monstruoWidth = 132;
    private final int monstruoHeight = 132;
    private Jugador player;
    private InfoPorts info;
    private Socket socketTCP;
    private ObjectOutputStream out;
    private boolean juegoIniciado = true;
    private Monstruito monstruoHilo;

    public Juego(Jugador player, InfoPorts info) throws IOException, JMSException {
        this.player = player;
        this.info = info;
        score = 0;

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(subject);

        init();
        initConnection();
        iniciaJuego();

        monstruoHilo = new Monstruito();
        monstruoHilo.start();
    }

    public void init() {
        setTitle("Pégale al monstruo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 100, 608, 720);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(0, 0, 0));
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(35, 105, 528, 529);
        panel.setLayout(null);
        contentPanel.add(panel);

        lblScore = new JLabel("Puntaje: 0");
        lblScore.setForeground(new Color(142, 53, 171));
        lblScore.setHorizontalAlignment(SwingConstants.TRAILING);
        lblScore.setFont(new Font("Cambria", Font.BOLD, 14));
        lblScore.setBounds(423, 54, 144, 33);
        contentPanel.add(lblScore);

        lblTimeLeft = new JLabel(player.getPlayerId());
        lblTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimeLeft.setForeground(new Color(203, 92, 230));
        lblTimeLeft.setFont(new Font("Cambria", Font.BOLD, 30));
        lblTimeLeft.setBounds(232, 54, 144, 33);
        contentPanel.add(lblTimeLeft);

        monstruoInImgRedo = resizeIcon(monstruoInImg, monstruoWidth, monstruoHeight);
        monstruoOutImgRedo = resizeIcon(monstruoOutImg, monstruoWidth, monstruoHeight);

        for (int i = 0, x = 0, y = 396; i < 16; i++) {
            btnMonstruos[i] = new JButton();
            btnMonstruos[i].setName(String.valueOf(i));
            btnMonstruos[i].setBounds(x, y, monstruoWidth, monstruoHeight);
            panel.add(btnMonstruos[i]);
            btnMonstruos[i].setIcon(monstruoInImgRedo);
            x = (x + monstruoWidth) % 528;
            y = x == 0 ? y - monstruoHeight : y;
            tablero[i] = false;
        }
        setContentPane(contentPanel);
    }

    private void initConnection() {
        try {
            String serverIP = this.info.getDirIP();
            int serverPort = this.info.getPortTCP();
            socketTCP = new Socket("localhost", 49152);
            out = new ObjectOutputStream(socketTCP.getOutputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static int creaMonstruo(int monstruoID) {
        tablero[monstruoID] = true;
        btnMonstruos[monstruoID].setIcon(monstruoOutImgRedo);
        return monstruoID;
    }

    private void clickMonstruo(int monstruoID) {
        if (juegoIniciado) {
            if (tablero[monstruoID]) {
                score++;
                this.player.setPlayerScore(score);
                btnMonstruos[monstruoID].setIcon(monstruoInImgRedo);
                tablero[monstruoID] = false;
                lblScore.setText(String.valueOf(score));
                try {
                    out.writeObject(this.player);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void iniciaJuego() {
        for (JButton monstruo : btnMonstruos) {
            monstruo.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    JButton btn = (JButton) e.getSource();
                    int monstruoID = Integer.parseInt(btn.getName());
                    clickMonstruo(monstruoID);
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

    public static void limpiaMonstruo(int monstruoID) {
        tablero[monstruoID] = false;
        btnMonstruos[monstruoID].setIcon(monstruoInImgRedo);
    }

}