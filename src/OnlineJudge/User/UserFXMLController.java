/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.OnlineJudge;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class UserFXMLController implements Initializable {

    @FXML
    private Text UserName;
    @FXML
    private AnchorPane Node;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("User fxml controller ini");
        OnlineJudge.Nodes=Node;
        UserName.setText(LocalUser.getUser().getName());
        // TODO
    }    

    @FXML
    private void HomeButtonClicked(ActionEvent event) throws IOException {
        System.out.println("Home buton clicked");
    }


    @FXML
    private void ProblemsetButtonClicked(ActionEvent event) {
        //System.out.println("Problemset button clicked");
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/ProblemSet/ProblemSetFXML.fxml"));
            Node.getChildren().removeAll(Node.getChildren());
            Node.getChildren().add(root);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void SubmitSolutionButtonClicked(ActionEvent event) {
        //System.out.println("Submit button clicked");
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/SubmitFXML.fxml"));
            Node.getChildren().removeAll(Node.getChildren());
            Node.getChildren().add(root);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void StatusButtonClicked(ActionEvent event) {
        //System.out.println("Status button clicked");
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/StatusFXML.fxml"));
            Node.getChildren().removeAll(Node.getChildren());
            Node.getChildren().add(root);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void MySubmissionButtonClicked(ActionEvent event) {
        
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/MySubmissionFXML.fxml"));
            Node.getChildren().removeAll(Node.getChildren());
            Node.getChildren().add(root);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void MyProfileButtonClicked(ActionEvent event) {
        //System.out.println("MyProfile button clicked");
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/MySubmissionFXML.fxml"));
            Node.getChildren().removeAll(Node.getChildren());
            Node.getChildren().add(root);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void LogOutButtonClicked(ActionEvent event) {
        
        //System.out.println("LogOut button clicked");
        try
        {
            System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void ContestButtonClicked(ActionEvent event) {
    }

    

    @FXML
    private void EditProblemButtonClicked(ActionEvent event) {
        //System.out.println("Edit button clicked");
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/ProblemSet/EditProblemFXML.fxml"));
            if(!(null == OnlineJudge.Nodes.getChildren()))
            OnlineJudge.Nodes.getChildren().removeAll(OnlineJudge.Nodes.getChildren());
            OnlineJudge.Nodes.getChildren().add(root);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            
        }
    }
    
}
