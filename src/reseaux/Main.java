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
	
	public static void main(String[] args) {
		String host="192.168.43.4";
		InetAddress address;
		ServerSocket serverSocket = null;
		Socket socketOfServer = null;
		

		try {
			//address=InetAddress.getByName(host);
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			//System.out.println(host+ " "+ address.getHostAddress());
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			serverSocket = new ServerSocket(4444);
			
		}
		catch(IOException e) {
			System.out.println("Couldn't listen on port 4444");
			System.exit(-1);
		}
		
		Socket clientSocket =null;
		try {
	           System.out.println("Server is waiting to accept user...");

	           // Accept client connection request
	           // Get new Socket at Server.    
	           clientSocket = serverSocket.accept();
	           System.out.println("Accept a client!");

	          
	           Socket echoSocket = null;
	           PrintWriter out= null;
	           BufferedReader in=null;
	           
	           
	           try {
	        	   echoSocket= new Socket("127.0.1.1",4444);
	        	   out = new PrintWriter(echoSocket.getOutputStream(),true);
	        	   in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
	        	   
	        	   
	           }catch(UnknownHostException e) {
	        	   System.err.println("Don't know about host taranis.");
	        	   System.exit(-1);
	           }catch(IOException e) {
	        	   System.err.println("Couldn't get I/O for "+"the connection to taranis");
	        	   System.exit(-1);
	           }

	          BufferedReader strIn=new BufferedReader(new InputStreamReader(System.in));

	          String userInput;
	          while((userInput=strIn.readLine()) != null) {
	        	  System.out.println(userInput);
	          
	          }
	          
	          
	       } catch (IOException e) {
	           System.out.println(e);
	           e.printStackTrace();
	       }
	       System.out.println("Sever stopped!");
	   }
	}
