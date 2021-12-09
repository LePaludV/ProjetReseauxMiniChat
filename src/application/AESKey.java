package application;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESKey implements Serializable {

    private Key key;

    /*
        Génère la clé AES
    */
    public AESKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            this.key = kg.generateKey();
            FileOutputStream fileOut = new FileOutputStream("AESKey.key");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(key);
            out.close();
            fileOut.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Récupère la clé AES depuis un fichier
    */
    public AESKey(File file) throws ClassNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        this.key = (Key) ois.readObject();
    }
    public AESKey(Key key) {
        this.key = key;
    }
    public AESKey(byte[] key) throws IOException, ClassNotFoundException {
        this.key = (Key) convertFromBytes(key);
    }

    public byte[] getKeyBytes() throws IOException {
        return convertToBytes(this.key);
    }

    private static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            for (byte b: Base64.getEncoder().encode(bos.toByteArray())) {
                System.out.print(b + " ");
            }
            System.out.println();
            return Base64.getEncoder().encode(bos.toByteArray());
        }
    }

    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        for(byte b: bytes) {
            System.out.print(b + " ");
        }
        System.out.println();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(bytes));
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }


    public byte[] encodeString(String text) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encode(cipher.doFinal(text.getBytes()));
    }

    public String decodeBytes(byte[] bytes) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(bytes)));
    }

    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException, ClassNotFoundException {
        AESKey aes = new AESKey();
        String bytes = new String(aes.encodeString("Salut"));
        AESKey aes3 = new AESKey(aes.getKeyBytes());
        for (byte b: aes3.getKeyBytes()) {
            System.out.print(b + " ");
        }
        System.out.println();
        String s = new String(aes3.getKeyBytes(), StandardCharsets.UTF_8);
        for (byte b: aes3.getKeyBytes()) {
            System.out.print(b + " ");
        }
        System.out.println();
        System.out.println(aes3.decodeBytes(bytes.getBytes()));
    }
}
