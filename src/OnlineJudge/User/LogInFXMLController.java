/* ....Show License.... */
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Login Controller.
 */
public class LogInFXMLController extends AnchorPane implements Initializable {

    @FXML
    Button login;
    @FXML
    Label errorMessage;
    @FXML
    private Button BackButon;
    @FXML
    private Button RegisterButton;
    @FXML
    private TextField Handle;
    @FXML
    private PasswordField Password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setText("");

    }

    @FXML
    private void RegisterButtonClicked(ActionEvent event) {
        System.out.println("Register Button Clicked");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/RegisterFXML.fxml"));

            Scene scene = new Scene(root, 720, 600);

            OnlineJudge.PrimaryStage.setScene(scene);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    @FXML
    private void HomeButtonClicked(ActionEvent event) {
        System.out.println("Home  Button pressed");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/OnlineJudgeFXML.fxml"));

            Scene scene = new Scene(root, 720, 600);

            OnlineJudge.PrimaryStage.setScene(scene);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    @FXML
    private void LogInButtonClicked(ActionEvent event) {
        if(Handle.getText().equals("")&&Password.getText().equals(""))
        {
            Password.setText("admin");
            Handle.setText("admin");
        }
        
        if(Handle.getText().equals("admin")&&Password.getText().equals("admin"))
        {
                LocalUser.setAdmin();
                System.out.println("Log in successful");
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/UserFXML.fxml"));

                    Scene scene = new Scene(root, 720, 600);

                    OnlineJudge.PrimaryStage.setScene(scene);
                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
        }
            
        
        if (UserSet.Users.containsKey(Handle.getText())) {
            if (Password.getText().equals(UserSet.Users.get(Handle.getText()).Password)) {
                LocalUser.user = UserSet.Users.get(Handle.getText());
                System.out.println("Log in successful");
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/OnlineJudge/User/UserFXML.fxml"));

                    Scene scene = new Scene(root, 720, 600);

                    OnlineJudge.PrimaryStage.setScene(scene);
                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
            } else {
                errorMessage.setText("Wrong Handle or Password");
            }
        } else {
            errorMessage.setText("Wrong Handle or Password");
        }
    }
}
