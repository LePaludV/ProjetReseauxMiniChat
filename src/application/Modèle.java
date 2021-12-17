package application;

import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;

import javafx.concurrent.Task;

public class Mod�le extends Observable {

	public Main main;
	public ChatEntre2Clients tcp;
	private String txt;
	public DatagramClient udp;
	
	public Mod�le (Main main) {
		
		this.main=main;
	}

	public void gototcp() throws IOException {
		System.out.println("re�u TCP Modele");

		
		this.main.loadTCP();
		// TODO Auto-generated method stub
		
	}

	public void gotoudp() throws IOException {
		System.out.println("re�u UDP Modele");
		

		Task<Void> task = new Task<Void>() {

			

			@Override
			public Void call() throws Exception {
			
				udp = new DatagramClient();
				
				return null;
			
				
			}
			
			
		};
		new Thread(task).start();
		
		this.main.loadUDP(udp);
		// TODO Auto-generated method stub
		
	}

	public void goToAcc() throws IOException {
		this.main.loadAcc();
		// TODO Auto-generated method stub
		
	}

	public void sendMsgTCP(String text) {
		System.out.println(text);
		setChanged();
		notifyObservers(text);
		// TODO Auto-generated method stub
		
	}

	public String getText() {
		
		// TODO Auto-generated method stub
		return txt;
	}

	public void receiveMsgTCP(String msg) {
		System.out.println(msg);
		this.main.ctrlTCP.showNewMsg(msg);
		// TODO Auto-generated method stub
		
	}

	
}
