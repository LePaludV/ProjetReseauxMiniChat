package application;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

@SuppressWarnings("deprecation")
public class ChatEntre2Clients implements Observer  {

    //127.0.1.1
	public Mod�le mdl;
    private ServerSocket serveurSocket;
    String s;
    //public final Scanner sc = new Scanner(mdl.getText());//pour lire à partir du clavier
    public final Scanner sc = new Scanner(System.in);
    private String ipServeur;
    private boolean receptKey;
    private AESKey aes;
    public String messageASend;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private final HashMap<String, String> repertoireIP;

  
    
    public AESKey getAes() {
        return aes;
    }
    public void setAes(AESKey aes) {
        this.aes = aes;
    }

    public boolean isServeur() {
    	
        return ipServeur.isEmpty();
    }

    private void ajoutIPServeur() {
    	ipServeur=this.mdl.main.AskClientServeur();
        /*System.out.println("A quelle IP voulez-vous communiquer ? ");
        System.out.println("(laissez vide pour devenir l'hôte (serveur)");
        System.out.print(" >> ");

        ipServeur = sc.nextLine();*/
    }

    private void creationClient() throws IOException {
        final Socket clientSocket = new Socket(ipServeur,5000);

        //flux pour envoyer
        final PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        try {
            out.println(InetAddress.getLocalHost().getHostAddress() + " connecté!");
            out.flush();
            receptKey = true;
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //flux pour recevoir
        final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        Thread envoyer = new Thread( () -> {
            while(true){
                String msg = sc.nextLine();
                try {
                	String receive = new String(getAes().encodeString(InetAddress.getLocalHost().getHostAddress() + " >> " + msg));
                    this.mdl.receiveMsgTCP(receive);
                    out.println(new String(msg));
                    out.flush();
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                if(msg.equals("bye"))
                    System.exit(0);
            }
        });
        envoyer.start();

        Thread recevoir = new Thread(() -> {
            try {
                String msg = in.readLine();
                while(msg!=null){
                    if(receptKey) {
                        setAes(new AESKey(msg.getBytes()));
                        System.out.println("AES reçu!");
                        receptKey = false;
                    }
                    else {
                        msg = getAes().decodeBytes(msg.getBytes());
                        if(msg.toLowerCase().contains("je m'appelle ")) {
                            //Ajout au répertoire d'IP
                            repertoireIP.put(msg.split(" >>")[0], msg.split("appelle ")[1]);
                        }
                        //Remplace les IPs par leurs noms dans le répertoire
                        for(Map.Entry<String, String> entry : repertoireIP.entrySet())
                            msg = msg.replaceAll(entry.getKey(), entry.getValue());
                        System.out.println(msg);
                    }
                    if(msg.equals("bye"))
                        System.exit(0);
                    msg = in.readLine();
                }
                System.out.println("Serveur déconnecté");
                out.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        });
        recevoir.start();
    }

    private void creationServeur() {
        if(!isServeur())
            System.out.println("Connexion refusée, ouverture de son propre serveurSocket");
        try {
            serveurSocket = new ServerSocket(5000);
            //Obtenir sa propre addresse:isServeur()
            try {
                System.out.println("Addresse de connexion: " + InetAddress.getLocalHost().getHostAddress());
            }
            catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
            //Création de la clé:
            aes = new AESKey();

            clientSocket = serveurSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Thread envoi = new Thread( () -> {
                while(true){
                	
                    messageASend =  sc.nextLine(); //nextLine: méthode bloquante
                    
                    try {
                    	System.out.println("----->"+messageASend);
                        out.println(new String(aes.encodeString(InetAddress.getLocalHost().getHostAddress() + " >> " + messageASend)));
                        out.flush();
                    }
                    catch (UnknownHostException | NoSuchPaddingException |
                           IllegalBlockSizeException | NoSuchAlgorithmException |
                           BadPaddingException | InvalidKeyException e1) {
                        e1.printStackTrace();
                    }
                    if(messageASend.equals("bye"))
                        System.exit(0);
                }
            });
            envoi.start();

            Thread recevoir = new Thread( () -> {
                while(true) {
                    if(clientSocket.isClosed()) {
                        try {
                            clientSocket = serveurSocket.accept();
                            out = new PrintWriter(clientSocket.getOutputStream());
                            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    try {
                        String msg = in.readLine();
                        //tant que le client est connecté
                        while (msg != null) {
                            if(msg.contains("connecté!")) {
                                System.out.println("Envoi de la clé...");
                                try {
                                    out.println(new String(aes.getKeyBytes(), StandardCharsets.UTF_8));
                                    out.flush();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            else {
                                msg = aes.decodeBytes(msg.getBytes());
                                if(msg.toLowerCase().contains("je m'appelle ")) {
                                    //Ajout au répertoire d'IP
                                    repertoireIP.put(msg.split(" >>")[0], msg.split("appelle ")[1]);
                                }
                                //Remplace les IPs par leurs noms dans le répertoire
                                for(Map.Entry<String, String> entry : repertoireIP.entrySet())
                                    msg = msg.replaceAll(entry.getKey(), entry.getValue());
                                System.out.println(msg);
                            }
                            if (msg.equals("bye"))
                                System.exit(0);
                            msg = in.readLine(); //readLine: méthode bloquante
                        }
                        //sortir de la boucle si le client a déconnecté
                        System.out.println("Client déconnecté");
                        //fermer le flux et la session socket
                        out.close();
                        clientSocket.close();
                        //serveurSocket.close();
                    } catch (IOException | NoSuchPaddingException |
                            IllegalBlockSizeException | NoSuchAlgorithmException |
                            BadPaddingException | InvalidKeyException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            recevoir.start();
        }catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public ChatEntre2Clients() {
    	
    	
        repertoireIP = new HashMap<>();
        ajoutIPServeur();
        try {
            if(isServeur())
                creationServeur();
            else
            	creationClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //new ChatEntre2Clients();
    }
	@Override
	public void update(Observable o, Object arg) {
		messageASend=(String) arg;
		System.out.println(messageASend);
		
	}
}