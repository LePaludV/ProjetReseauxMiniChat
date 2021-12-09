
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class chatMulti {

    //127.0.1.1

    private final MulticastSocket socket;
    final Scanner sc = new Scanner(System.in);//pour lire à partir du clavier
    private String ipServeur;
    private final int port;
    private boolean receptKey;
    private AESKey aes;

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
        System.out.println("A quelle IP voulez-vous communiquer ? ");
        System.out.println("(laissez vide pour devenir l'hôte (serveur)");
        System.out.print(" >> ");

        ipServeur = sc.nextLine();
    }

    private void creationClient() throws IOException {
        ipServeur = "230.0.0.1";
        InetAddress group = InetAddress.getByName(ipServeur);
        socket.joinGroup(group);
        System.out.println(socket.getPort());
        byte[] buf = new byte[256];

        try {
            byte[] msgBytes = (InetAddress.getLocalHost().getHostAddress() + " connecté!").getBytes(StandardCharsets.ISO_8859_1);
            System.arraycopy(msgBytes, 0, buf, 0, msgBytes.length);
            socket.send(new DatagramPacket(buf, msgBytes.length, group, port));
            receptKey = true;
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Thread envoyer = new Thread( () -> {
            while(true){
                String msg = sc.nextLine();
                try {
                    byte[] msgBytes = aes.encodeString(InetAddress.getLocalHost().getHostAddress() + " >> " + msg);
                    System.arraycopy(msgBytes, 0, buf, 0, msgBytes.length);
                    socket.send(new DatagramPacket(buf, msgBytes.length, group, port));
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(msg.equals("bye"))
                    System.exit(0);
            }
        });
        envoyer.start();

        Thread recevoir = new Thread(() -> {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String msg = new String(packet.getData());
                while(true){
                    if(receptKey) {
                        System.out.println("Réception AES...");
                        System.out.println(msg);
                        setAes(new AESKey(msg));
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
                    if(msg.equals("bye")) {
                        System.out.println("Serveur déconnecté");
                        socket.close();
                        System.exit(0);
                    }
                }
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
            System.out.println("Connexion refusée, ouverture de son propre MulticastSocket");
        try {
            //Obtenir sa propre addresse:
            ipServeur="230.0.0.1";
            System.out.println("Addresse de connexion: " + ipServeur);
            //Création de la clé:
            aes = new AESKey();

            InetAddress group = InetAddress.getByName(ipServeur);
            socket.joinGroup(group);
            System.out.println(socket.getPort());
            Thread envoi = new Thread( () -> {
                byte[] buf = new byte[256];
                while(true){
                    String msg = sc.nextLine(); //nextLine: méthode bloquante
                    try {
                        byte[] msgBytes = aes.encodeString(InetAddress.getLocalHost().getHostAddress() + " >> " + msg);
                        System.arraycopy(msgBytes, 0, buf, 0, msgBytes.length);
                        socket.send(new DatagramPacket(buf, msgBytes.length, group, port));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    if(msg.equals("bye"))
                        System.exit(0);
                }
            });
            envoi.start();

            Thread recevoir = new Thread( () -> {
                byte[] buf = new byte[256];
                while(true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        String msg = new String(packet.getData(), StandardCharsets.ISO_8859_1);
                        //tant que le client est connecté
                        while (true) {
                            if(msg.contains("connecté!")) {
                                System.out.println("Envoi de la clé...");
                                try {
                                    byte[] msgBytes = aes.getKey().getBytes();
                                    System.arraycopy(msgBytes, 0, buf, 0, msgBytes.length);
                                    socket.send(new DatagramPacket(buf, msgBytes.length, group, port));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            else {
                                System.out.println(new String(msg.getBytes(StandardCharsets.ISO_8859_1)));
                                msg = aes.decodeBytes(msg.getBytes(StandardCharsets.ISO_8859_1));
                                if(msg.toLowerCase().contains("je m'appelle ")) {
                                    //Ajout au répertoire d'IP
                                    repertoireIP.put(msg.split(" >>")[0], msg.split("appelle ")[1]);
                                }
                                //Remplace les IPs par leurs noms dans le répertoire
                                for(Map.Entry<String, String> entry : repertoireIP.entrySet())
                                    msg = msg.replaceAll(entry.getKey(), entry.getValue());
                                System.out.println(msg);
                            }
                            if (msg.equals("bye")) {
                                //sortir de la boucle si le client a déconnecté
                                System.out.println("Client déconnecté");
                                //fermer la session socket
                                socket.close();
                                System.exit(0);
                            }
                            socket.receive(packet);
                            msg = new String(packet.getData());
                        }
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

    public ChatMultiClients() throws IOException {
        port = 5000;
        socket = new MulticastSocket(port);
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

    public static void main(String[] args) throws IOException {
        new ChatMultiClients();
    }
}
