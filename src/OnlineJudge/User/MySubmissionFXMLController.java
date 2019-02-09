/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.Submission.Submission;
import OnlineJudge.Submission.SubmissionSet;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class MySubmissionFXMLController implements Initializable {

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
        
        SubmissionId.setCellValueFactory(new PropertyValueFactory<Submission,Integer>("Id"));
        
        SubmissionTime.setCellValueFactory(new PropertyValueFactory<Submission,String>("Time"));
        
        UserHandle.setCellValueFactory(new PropertyValueFactory<Submission,String>("Handle"));
        
        ProblemName.setCellValueFactory(new PropertyValueFactory<Submission,String>("ProblemName"));
        
        Language.setCellValueFactory(new PropertyValueFactory<Submission,String>("Language"));
        
        Verdict.setCellValueFactory(new PropertyValueFactory<Submission,String>("Verdict"));
        
        TimeTaken.setCellValueFactory(new PropertyValueFactory<Submission,String>("TimeTaken"));
        
                
        ObservableList<Submission > data = FXCollections.observableArrayList();
        //System.out.println("User "+LocalUser.getUser());
        for(Integer id : LocalUser.getUser().getMySubmissions())
            data.add(SubmissionSet.Submissions.get(id));
        
        StatusTable.setItems(data);
        StatusTable.getSortOrder().add(SubmissionId);
    }    
    
}
