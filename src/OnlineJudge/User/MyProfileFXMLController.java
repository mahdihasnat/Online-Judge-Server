/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class MyProfileFXMLController implements Initializable {

    @FXML
    private Label Name;
    @FXML
    private Label Handle;
    @FXML
    private Label Mail;
    @FXML
    private Label Country;
    @FXML
    private Label University;
    @FXML
    private Label Password;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("Profile ini");
        Name.setText(LocalUser.getUser().getName());
        Handle.setText(LocalUser.getUser().getHandle());
        Mail.setText(LocalUser.getUser().getEmail());
        Country.setText(LocalUser.getUser().getCountry());
        University.setText(LocalUser.getUser().getUniversity());
        Password.setText(LocalUser.getUser().getPassword());
        Password.setVisible(false);
        
    }    


    @FXML
    private void FlipPassword(ActionEvent event) {
        if(Password.isVisible())
        {
            Password.setVisible(false);
        }
        else 
            Password.setVisible(true);
    }
    
}
