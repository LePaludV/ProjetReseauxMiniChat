package application;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    	Label l =new Label("Vous : "+text.getText());
    	vBoxAffichage.getChildren().add(l);
    	//System.out.println(text.getText());
    	this.mdl.sendMsgTCP(text.getText());
    }



	public void showNewMsg(String msg) {
		Label l =new Label("Vous : "+text.getText());
    	vBoxAffichage.getChildren().add(l);
		// TODO Auto-generated method stub
		
	}


}