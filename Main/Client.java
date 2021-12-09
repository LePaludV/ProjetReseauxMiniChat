import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);//pour lire à partir du clavier

        try {
            /*
             * les informations du serveur ( port et adresse IP ou nom d'hote
             * 127.0.0.1 est l'adresse local de la machine
             */
            clientSocket = new Socket("127.0.1.1",5000);

            //flux pour envoyer
            out = new PrintWriter(clientSocket.getOutputStream());
            //flux pour recevoir
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread envoyer = new Thread( () -> {
                while(true){
                    String msg = sc.nextLine();
                    out.println(msg);
                    out.flush();
                    if(msg.equals("bye"))
                        System.exit(0);
                }
            });
            envoyer.start();

            Thread recevoir = new Thread(() -> {
                try {
                    String msg = in.readLine();
                    while(msg!=null){
                        System.out.println("Serveur : "+msg);
                        if(msg.equals("bye"))
                            System.exit(0);
                        msg = in.readLine();
                    }
                    System.out.println("Serveur déconnecté");
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recevoir.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}