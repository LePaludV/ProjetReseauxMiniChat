package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	
	

	public Modèle mdl;
	public Controller ctrl;
	public ControllerTCP ctrlTCP;
	public Stage stage;
	
	
	
	public Main() {
		this.mdl=new Modèle(this);
		this.ctrl = new Controller(this.mdl);
		this.ctrlTCP= new ControllerTCP(this.mdl);
		this.stage=new Stage();
		
		
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

	public void loadTCP(ChatEntre2Clients tcp) throws IOException {
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
        this.mdl.addObserver(tcp);

		
		// TODO Auto-generated method stub
		
	}
 
	

	public void loadUDP() {
		System.out.println("recu main UDP");

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

	
}
