package Cliente;
import java.util.Random;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import static javax.swing.JOptionPane.showMessageDialog;

public class Monstruito extends Thread {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "Monstruito_Americanista";
    Random r = new Random();
    public Monstruito() {
        System.out.println("Entró");
    }
    @Override
    public void run() {
        while (true) {
            int topoID = -1;
            byte[] buffer = new byte[1000];
            while(true){
                try {
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
                    Connection connection = connectionFactory.createConnection();
                    connection.start();
                    Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
                    Destination destination = session.createTopic(subject);
                    MessageConsumer messageConsumer = session.createConsumer(destination);
                    TextMessage messageIn = (TextMessage) messageConsumer.receive();
                    if (topoID>=0) {
                        Juego.limpiaTopo(topoID);
                    }
                    String msjRecibido = messageIn.getText();
                    System.out.println("Message: " + msjRecibido);
                    if(msjRecibido.charAt(0) != '_') {//No ha habido ganador
                        topoID = Integer.parseInt(msjRecibido);
                        if (topoID > 15) {
                            topoID = r.nextInt(16);
                        }
                        Juego.creaTopo(topoID);
                    }
                    else {//Hubo ganador
                        Juego.score = 0;
                        showMessageDialog(null, "Gana " + msjRecibido);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}