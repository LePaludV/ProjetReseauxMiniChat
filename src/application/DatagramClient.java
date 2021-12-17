package application;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SuppressWarnings("deprecation")
public class DatagramClient implements Observer {

    private final MulticastSocket socket;
    final Scanner sc = new Scanner(System.in);//pour lire à partir du clavier
    private String ipServeur;
    private final int port;
    private boolean receptKey;
    private AESKey aes;
    private String idChat;
    private final HashMap<String, String> repertoire;

    private String genererIdChat() {
        int leftLimit = '0';
        int rightLimit = 'z';
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void setAes(AESKey aes) {
        this.aes = aes;
    }
    public AESKey getAes() { return this.aes; }

    public boolean isServeur() {
        return ipServeur.isEmpty();
    }

    private void ajoutIPServeur() {
        System.out.println("A quelle IP voulez-vous communiquer ? ");
        System.out.println("(laissez vide pour devenir l'hôte (serveur)");
        System.out.print(" >> ");

        ipServeur = sc.nextLine();
    }

    public static byte[] formatBytes(byte[] buf) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
        byte[] tmp = dis.readAllBytes();
        List<Byte> tmp2 = new ArrayList<>();
        for(byte b : tmp) {
            if(b != 0)
                tmp2.add(b);
        }
        byte[] key = new byte[tmp2.size()];
        System.arraycopy(tmp, 0, key, 0, key.length);
        return key;
    }

    private void creationClient() throws IOException {
        InetAddress group = InetAddress.getByName(ipServeur);
        try {
            ByteArrayOutputStream sortie = new ByteArrayOutputStream();
            (new DataOutputStream(sortie)).writeBytes(idChat + " connecté!");
            byte[] msgBytes = sortie.toByteArray();
            socket.send(new DatagramPacket(msgBytes, msgBytes.length, group, port));
            socket.joinGroup(group);
            receptKey = true;
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Thread envoyer = new Thread( () -> {
            while(true){
                String msg = sc.nextLine();
                try {
                    socket.leaveGroup(group);
                    ByteArrayOutputStream sortie = new ByteArrayOutputStream();
                    (new DataOutputStream(sortie)).writeBytes(idChat + " >> " + msg);
                    byte[] msgBytes = getAes().encodeString(sortie.toString(Charset.forName("ISO_8859_1")));

                    socket.send(new DatagramPacket(msgBytes, msgBytes.length, group, port));
                    socket.joinGroup(group);
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    System.err.println("Mauvaise clé de crytage/décryptage utilisé... Fermeture du programme...");
                    System.exit(-1);
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
                while(true){
                    byte[] buf = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(formatBytes(buf), StandardCharsets.ISO_8859_1);
                    if(receptKey) {
                        try {
                            setAes(new AESKey(formatBytes(buf)));
                        }
                        catch (IllegalArgumentException ignore) {
                            System.err.println("Erreur d'argument à la création de la de l'objet AESKey.");
                        }
                        receptKey = false;
                    }
                    else {
                        try {
                            msg = getAes().decodeBytes(msg.getBytes(StandardCharsets.ISO_8859_1)); //TODO HERE Message reçu (serveur)
                            if(msg.toLowerCase().contains("je m'appelle ")) {
                                //Ajout au répertoire
                                repertoire.put(msg.split(" >>")[0], msg.split("appelle ")[1]);
                            }
                            //Remplace les noms du répertoire
                            for(Map.Entry<String, String> entry : repertoire.entrySet())
                                msg = msg.replaceAll(entry.getKey(), entry.getValue());
                            System.out.println(msg);
                            if (msg.toLowerCase().contains(">> bye")) {
                                //sortir de la boucle si le serveur a déconnecté
                                System.out.println("Serveur déconnecté");
                                //fermer le flux et la session socket
                                socket.close();
                                System.exit(0);
                            }
                        }
                        catch (IllegalBlockSizeException ignored) {/*C'est la clé de décryptage*/}
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                System.err.println("Mauvaise clé de crytage/décryptage utilisé... Fermeture du programme...");
                System.exit(-1);
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
            ipServeur="230.0.0.1";
            System.out.println("Addresse de connexion: " + ipServeur);
            //Création de la clé:
            setAes(new AESKey());

            InetAddress group = InetAddress.getByName(ipServeur);
            socket.joinGroup(group);
            Thread envoi = new Thread( () -> {
                while(true){
                    String msg = sc.nextLine(); //nextLine: méthode bloquante
                    try {
                        socket.leaveGroup(group);
                        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
                        (new DataOutputStream(sortie)).writeBytes(idChat + " >> " + msg);
                        byte[] msgBytes = getAes().encodeString(sortie.toString(Charset.forName("ISO_8859_1")));
                        socket.send(new DatagramPacket(msgBytes, msgBytes.length, group, port));
                        socket.joinGroup(group);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        System.err.println("Mauvaise clé de crytage/décryptage utilisé... Fermeture du programme...");
                        System.exit(-1);
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    if(msg.equals("bye")) {
                        socket.close();
                        System.exit(0);
                    }
                }
            });
            envoi.start();

            Thread recevoir = new Thread( () -> {
                while(true) {
                    try {
                        //tant que le client est connecté
                        while (true) {
                            byte[] buf = new byte[256];
                            DatagramPacket packet = new DatagramPacket(buf, buf.length);
                            socket.receive(packet);
                            String msg = new String(formatBytes(buf), StandardCharsets.ISO_8859_1);
                            if(msg.contains("connecté!")) {
                                Thread envoiCle = new Thread( () -> {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) { e.printStackTrace(); }
                                    try {
                                        //socket.leaveGroup(group);
                                        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
                                        (new DataOutputStream(sortie)).write(getAes().getKeyBytes());
                                        byte[] msgBytes = sortie.toByteArray();
                                        socket.send(new DatagramPacket(msgBytes, msgBytes.length, group, port));
                                        //socket.joinGroup(group);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                });
                                envoiCle.start();
                            }
                            else {
                                try {
                                    msg = getAes().decodeBytes(msg.getBytes(StandardCharsets.ISO_8859_1)); //TODO HERE Message reçu (serveur)
                                    if(msg.toLowerCase().contains("je m'appelle ")) {
                                        //Ajout au répertoire
                                        repertoire.put(msg.split(" >>")[0], msg.split("appelle ")[1]);
                                    }
                                    //Remplace les noms du répertoire
                                    for(Map.Entry<String, String> entry : repertoire.entrySet())
                                        msg = msg.replaceAll(entry.getKey(), entry.getValue());
                                    System.out.println(msg);
                                    if(msg.toLowerCase().contains(">> bye")) {
                                        //Indique qu'un client a déconnecté
                                        System.out.println("Client déconnecté");
                                    }
                                }
                                catch (IllegalBlockSizeException ignored) {/*C'est la clé de décryptage*/}
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        System.err.println("Mauvaise clé de crytage/décryptage utilisé... Fermeture du programme...");
                        System.exit(-1);
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }
            });
            recevoir.start();
        }catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public DatagramClient() throws IOException {
        repertoire = new HashMap<>();
        port = 5000;
        socket = new MulticastSocket(port);
        idChat = genererIdChat();
        System.out.println(idChat);
        idChat = genererIdChat();
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
        new DatagramClient();
    }

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
