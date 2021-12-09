package application;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class Controller {
	public Modèle mdl;

    @FXML
    private Button btnTCP;

    @FXML
    private Button btnUDP;
    
    public Controller(Modèle mdl) {
    	this.mdl=mdl;
    }

    @FXML
    void goToTCP(ActionEvent event) throws IOException {
    	System.out.println("Going to TCP Chat");
    	
    	this.mdl.gototcp();
    	

    }

    @FXML
    void goToUDP(ActionEvent event) {
    	System.out.println("Going to UDP Chat");
    	this.mdl.gotoudp();

    	

    }

}
