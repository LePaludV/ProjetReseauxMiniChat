package reseaux;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;	

public class Main {
	static InetAddress address;
	static ServerSocket serverSocket = null;
	static Socket socketOfServer = null;
	static Socket clientSocket =null;
	static Socket echoSocket = null;
	static PrintWriter out= null;
    static BufferedReader in=null;
	
	
	public static void Start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		
		System.out.println("Server is waiting to accept user...");

        // Accept client connection request
        // Get new Socket at Server.    
        clientSocket = serverSocket.accept();
        
        
        
        
        System.out.println("Accept a client!");
        
        //reception msg
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));      
        String userInput;
        while((userInput=in.readLine()) != null) {
      	  System.out.println(userInput);
        };
        
        String ip=clientSocket.getInetAddress().getHostAddress();
        System.out.println(ip);
        
        
        //Envoie msg
        echoSocket= new Socket(ip,4445);
        out = new PrintWriter(echoSocket.getOutputStream(),true);
        
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(echoSocket.getOutputStream()));
        		
        bw.write("Salut");
        bw.flush();
        bw.close();
        
        
        Stop();
	}
	
	public static void Stop() throws IOException {
		out.close();
        in.close();
        
        echoSocket.close();
	};
	
		
	
	public static void main(String[] args) throws IOException {
		String host="192.168.43.4";
		//address=InetAddress.getByName(host);
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		//System.out.println(host+ " "+ address.getHostAddress());
		Start(4444);
		
		
		
		
		
	          
	        	  
	        	   
	} 
	}
