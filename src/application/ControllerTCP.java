package application;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
public class ControllerTCP {
	Modèle mdl;
	public ControllerTCP(Modèle mdl) {
		this.mdl=mdl;
		// TODO Auto-generated constructor stub
	}

    @FXML
    private Button btnBAcj;

    @FXML
    private TextField text;

    @FXML
    private Button sendBtn;

    @FXML
    private VBox vBoxAffichage;

    @FXML
    
    void ClickBtnBack(MouseEvent event) throws IOException {
    	System.out.println("Go back to acceuil");
    	this.mdl.goToAcc();
    }
 
  

    @FXML
    void PressBtn(MouseEvent event) {
    	System.out.println(text.getText());
    	this.mdl.sendMsgTCP(text.getText());
    }


}