package application;

import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;

import javafx.concurrent.Task;

public class Modèle extends Observable {

	public Main main;
	public ChatEntre2Clients tcp;
	private String txt;
	
	public Modèle (Main main) {
		
		this.main=main;
	}

	public void gototcp() throws IOException {
		System.out.println("reçu TCP Modele");
		
		Task<Void> task = new Task<Void>() {

			

			@Override
			protected Void call() throws Exception {
			
				
				tcp = new ChatEntre2Clients();
				return null;
			
				
			}
			
			
		};
		new Thread(task).start();
		
		
		this.main.loadTCP(tcp);
		// TODO Auto-generated method stub
		
	}

	public void gotoudp() {
		System.out.println("reçu UDP Modele");
		this.main.loadUDP();
		// TODO Auto-generated method stub
		
	}

	public void goToAcc() throws IOException {
		this.main.loadAcc();
		// TODO Auto-generated method stub
		
	}

	public void sendMsgTCP(String text) {
		setChanged();
		notifyObservers(text);
		// TODO Auto-generated method stub
		
	}

	public String getText() {
		
		// TODO Auto-generated method stub
		return txt;
	}
}
