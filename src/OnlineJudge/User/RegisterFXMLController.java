/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.OnlineJudge;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class RegisterFXMLController implements Initializable {

    @FXML
    private TextField Handle;
    @FXML
    private TextField Email;
    @FXML
    private TextField Country;
    @FXML
    private TextField University;
    @FXML
    private PasswordField Password;
    @FXML
    private TextField Name;
    @FXML
    private Button SubmitButton;
    @FXML
    private Label PromptLavel;
    @FXML
    private Button LogInButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void RegisterAccount(ActionEvent event) {
        System.out.println("Submit button Clicked");
        PromptLavel.setText("");
        if(Name.getText().length()<5) 
        {
            PromptLavel.setText("Enter Valid Name");
            return ;
               
        }
        else if(Handle.getText().length()<5)
        {
            PromptLavel.setText("Enter Valid Handle");
            return ;
        }
        else if(Email.getText().indexOf('@')==-1||Email.getText().indexOf('.')==-1)
        {
            PromptLavel.setText("Enter Valid Email");
            return ;
        }
        else if(Password.getText().length()<7)
        {
            PromptLavel.setText("Enter Valid Password");
            return ;
        }
        else if((UserSet.Users.containsKey(Handle.getText())))
        {
           PromptLavel.setText("Try Another Handle");
        }
        else
        {
            PromptLavel.setText("Registration Success");
            UserSet.Users.put(Handle.getText(), new User(Name.getText(), Handle.getText(), Email.getText(), Country.getText(), University.getText(), Password.getText()));
        }
    }

    @FXML
    private void LogInButtonCreated(ActionEvent event) {
        System.out.println("Log In Button pressed");
        try 
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/LogInFXML.fxml"));

            Scene scene = new Scene(root, 720, 600);

            OnlineJudge.PrimaryStage.setScene(scene);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        
    }
    
}
