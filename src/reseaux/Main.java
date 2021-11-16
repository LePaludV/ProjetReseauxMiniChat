package reseaux;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) {
		String host="localhost";
		InetAddress address;
		try {
			address=InetAddress.getByName(host);
			System.out.println(host+ " "+ address.getHostAddress());
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		try {
			ServerSocket serverSocket = new ServerSocket(4444);
			
		}
		catch(IOException e) {
			System.out.println("Couldn't listen on port 4444");
			System.out.println(-1);
		}
		

	}

}
