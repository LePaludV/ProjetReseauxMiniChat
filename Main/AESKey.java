

import javax.crypto.*;
import java.io.*;
import java.nio.charset.Charset;
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
    public AESKey(String key) throws IOException, ClassNotFoundException {
        this.key = (Key) convertFromString(key);
    }

    public String getKey() throws IOException {
        return convertToString(this.key);
    }

    private static String convertToString(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toString(Charset.forName("ISO_8859_1"));
        }
    }

    private static Object convertFromString(String str) throws IOException, ClassNotFoundException {
        System.out.println(str);
        byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
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
        AESKey aes3 = new AESKey(aes.getKey());
        for (byte b: aes3.getKey().getBytes(StandardCharsets.ISO_8859_1)) {
            System.out.print(b + " ");
        }
        System.out.println();
        String s = new String(aes3.getKey().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        for (byte b: aes3.getKey().getBytes(StandardCharsets.ISO_8859_1)) {
            System.out.print(b + " ");
        }
        System.out.println();
        for (byte b: s.getBytes(StandardCharsets.ISO_8859_1)) {
            System.out.print(b + " ");
        }
        System.out.println();
        AESKey aes4 = new AESKey(s);
        System.out.println(aes3.decodeBytes(bytes.getBytes()));
        System.out.println(aes4.decodeBytes(bytes.getBytes()));
    }
}
