/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.OnlineJudge;
import OnlineJudge.Submission.Submission;
import OnlineJudge.Submission.SubmissionSet;
import OnlineJudge.Submission.SubmissionShowFXMLController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author WNL
 */
public class StatusFXMLController implements Initializable {

    @FXML
    private TableColumn<Submission, Integer > SubmissionId;
    @FXML
    private TableColumn<Submission, String > SubmissionTime;
    @FXML
    private TableColumn<Submission, String > UserHandle;
    @FXML
    private TableColumn<Submission, String > ProblemName;
    @FXML
    private TableColumn<Submission, String > Language;
    @FXML
    private TableColumn<Submission, String > Verdict;
    @FXML
    private TableColumn<Submission, String > TimeTaken;
    @FXML
    private TableView<Submission> StatusTable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // System.out.println("Table View ini");
        
        
        SubmissionId.setCellValueFactory(new PropertyValueFactory<Submission,Integer>("Id"));
        
        SubmissionTime.setCellValueFactory(new PropertyValueFactory<Submission,String>("Time"));
        
        UserHandle.setCellValueFactory(new PropertyValueFactory<Submission,String>("Handle"));
        
        ProblemName.setCellValueFactory(new PropertyValueFactory<Submission,String>("ProblemName"));
        
        Language.setCellValueFactory(new PropertyValueFactory<Submission,String>("Language"));
        
        Verdict.setCellValueFactory(new PropertyValueFactory<Submission,String>("Verdict"));
        
        TimeTaken.setCellValueFactory(new PropertyValueFactory<Submission,String>("TimeTaken"));
        
        
        
        ObservableList<Submission > data = FXCollections.observableArrayList();
        
        data.addAll(SubmissionSet.Submissions.values());
        
        StatusTable.setItems(data);
        StatusTable.getSortOrder().add(SubmissionId);
    }    

    @FXML
    private void ShowSubmissionClicked(MouseEvent event) throws IOException {
        
        
        
        Submission SelectedSubmission= StatusTable.getSelectionModel().getSelectedItem();
        if(SelectedSubmission==null ) return ;
        FXMLLoader loader =new FXMLLoader(getClass().getResource("/OnlineJudge/Submission/SubmissionShowFXML.fxml"));
        Parent root = loader.load();
        SubmissionShowFXMLController controller = loader.getController();
        controller.setSubmission(SelectedSubmission);
        OnlineJudge.Nodes.getChildren().removeAll(OnlineJudge.Nodes.getChildren());
        OnlineJudge.Nodes.getChildren().add(root);
        
    }
    
}
