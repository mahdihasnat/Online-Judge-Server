/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.ProblemSet;

import OnlineJudge.User.SubmitFXMLController;
import java.nio.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import com.sun.webkit.dom.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static  OnlineJudge.OnlineJudge.Nodes;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class ProblemShowFXMLController implements Initializable {

    private Problem problem;
    private WebEngine engine;
    public static final File path = new File("ProblemSet");
    static final String FileSeparator = System.getProperty("file.separator");
    private File source;
    @FXML
    private Label ProblemName;
    @FXML
    private Label TimeLimit;
    @FXML
    private Label MemoryLimit;
    @FXML
    private TextFlow Statement;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
        ProblemName.setText(problem.getId()+" : "+problem.getName());
        TimeLimit.setText(problem.getTimeLimit().toString());
        MemoryLimit.setText(problem.getMemoryLimit().toString());
        if(problem.getStatementFile().getName().endsWith(".txt"))
        {
            Statement.getChildren().add(new Text(ReadFile(problem.getStatementFile())));
        }
        else 
        {
            try {
                Desktop d= Desktop.getDesktop();
                d.open(problem.getStatementFile());
            } catch (IOException ex) {
                Logger.getLogger(ProblemShowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        

    }
    
    static private String ReadFile(File f) {
        String src = "";
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bir = new BufferedInputStream(fis);
            int c = 1;
            while ((c = bir.read()) != -1) {

                //System.out.print((char) c);
                src += Character.toString((char) c);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return src;
    }

    @FXML
    private void SubmitButtonClicked(ActionEvent event) {
        try {
            if(problem == null) return ;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OnlineJudge/User/SubmitFXML.fxml"));
            Parent root = loader.load();
            SubmitFXMLController controller = loader.getController();
            controller.setProblem(problem);
            Nodes.getChildren().removeAll(Nodes.getChildren());
            Nodes.getChildren().add(root);
            
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
            
            
        } catch (IOException ex) {
            Logger.getLogger(ProblemShowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
