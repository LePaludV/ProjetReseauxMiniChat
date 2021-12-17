package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	
	

	public Modèle mdl;
	public Controller ctrl;
	public ControllerTCP ctrlTCP;
	public Stage stage;
	public controllerUDP ctrlUDP;
	
	
	public Main() {
		this.mdl=new Modèle(this);
		this.ctrl = new Controller(this.mdl);
		this.ctrlTCP= new ControllerTCP(this.mdl);
		this.stage=new Stage();
		this.ctrlUDP=new controllerUDP(this.mdl);
		
	}

	public void start(Stage stage) throws IOException {
		loadAcc();
		/*try {
			
			
			  FXMLLoader loader = new FXMLLoader();
		      loader.setLocation(getClass().getResource("Main.fxml"));
		      loader.setController(this.ctrl);
		      Parent root = loader.load();
		      
		      Scene scene = new Scene(root);
		      
		      stage.setScene(scene);
		      stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public static void main(String[] args) {
		launch(args);
		
		
		
	}

	@SuppressWarnings("deprecation")
	public void loadTCP() throws IOException {
		if(stage.isShowing()) {
			stage.close();
		}
		System.out.println("recu main TCP");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("TCP.fxml"));
		loader.setController(this.ctrlTCP);
		Parent root = loader.load();
	    Scene scene = new Scene(root);
	  
	    
        stage.setScene(scene);
        
        stage.show();
        
        String ip=AskClientServeur();

			
        ChatEntre2Clients tcp = new ChatEntre2Clients();
				

		
		
        this.mdl.addObserver(tcp);

		
		// TODO Auto-generated method stub
		
	}

	

	public void loadUDP(DatagramClient udp) throws IOException {
		
		if(stage.isShowing()) {
			stage.close();
		}
		System.out.println("recu main UDp");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("TCP.fxml"));
		loader.setController(this.ctrlUDP);
		Parent root = loader.load();
	    Scene scene = new Scene(root);
	  
	    
        stage.setScene(scene);
        stage.show();
        this.mdl.addObserver(udp);
		
	

		// TODO Auto-generated method stub
		
	}

	public void loadAcc() throws IOException {
		if(stage.isShowing()) {
			stage.close();
		}
		
		System.out.println("Chargement Acceuil");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Main.fxml"));
		loader.setController(this.ctrl);
		Parent root = loader.load();
	    Scene scene = new Scene(root);
	
        stage.setScene(scene);
        stage.show();
        
		// TODO Auto-generated method stub
		
	}

	public String AskClientServeur() {
		//Création d'une popup pour demander si il veut être client ou serveur
		String ip = new String();
		Popup pop=new Popup();
		Button buttonClient = new Button("Client");
		Button buttonServeur = new Button("Serveur"); 
		VBox box = new VBox();
        Label label = new Label("Voulez vous être le client ou le serveur ?");
        HBox hbox = new HBox();
        hbox.getChildren().addAll(buttonServeur,buttonClient);
        box.getChildren().addAll(label,hbox);
        pop.getContent().add(box);
        
        
        buttonClient.setOnMouseClicked(new EventHandler<>() {
            public void handle(MouseEvent event) {
            	//Si il choisi client demande de rentrer l'ip du serveur
                System.out.println("Client");
                pop.hide();
                TextField txt=new TextField ("Ip du serveur :");
                Button btnOk =new Button("Ok");
                VBox box = new VBox();
                box.getChildren().addAll(txt,btnOk);
                Popup pop=new Popup();
                pop.getContent().add(box);
                pop.show(stage);
                btnOk.setOnMouseClicked(new EventHandler<>() {
                    public void handle(MouseEvent event) {
                    	String ip=txt.getText();
                    	System.out.println(ip);
                        pop.hide();
                        
                    }
                });
               
            }
        });
        
        buttonServeur.setOnMouseClicked(new EventHandler<>() {
            public void handle(MouseEvent event) {
                System.out.println("Serveur");
                pop.hide();
                
            }
        });
        
        pop.show(stage);
        stage.show();
        
		// TODO Auto-generated method stub
		return ip;
	}

	
}
