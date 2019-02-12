/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.Submission.Submission;
import OnlineJudge.Submission.SubmissionSet;
import OnlineJudge.Submission.SubmissionShowFXMLController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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
        
        SubmissionId.setCellValueFactory(new PropertyValueFactory<Submission, Integer>("Id"));

        SubmissionTime.setCellValueFactory(new PropertyValueFactory<Submission, String>("Time"));

        UserHandle.setCellValueFactory(new PropertyValueFactory<Submission, String>("Handle"));

        ProblemName.setCellValueFactory(new PropertyValueFactory<Submission, String>("ProblemName"));

        Language.setCellValueFactory(new PropertyValueFactory<Submission, String>("Language"));

        Verdict.setCellValueFactory(new PropertyValueFactory<Submission, String>("Verdict"));

        TimeTaken.setCellValueFactory(new PropertyValueFactory<Submission, String>("TimeTaken"));

        Verdict.setCellFactory(new Callback<TableColumn<Submission, String>, TableCell<Submission, String>>() {
            @Override
            public TableCell<Submission, String> call(TableColumn<Submission, String> param) {
                return new TableCell<Submission, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            if (item.equalsIgnoreCase("accepted")) {
                                this.setTextFill(Paint.valueOf("green"));
                            } else if (item.equalsIgnoreCase("wrong answer")) {
                                this.setTextFill(Paint.valueOf("red"));
                            }

                            setText(item);

                        }
                    }
                };
            }
        });

        ObservableList<Submission> data = FXCollections.observableArrayList();

        
        for(Submission s : SubmissionSet.Submissions.values())
        {
            if(s.getHandle().equals(LocalUser.getUser().getHandle()))
                data.add(s);
        }
        

        StatusTable.setItems(data);
        StatusTable.getSortOrder().add(SubmissionId);
        task();
    }    
    
    @FXML
    private void ShowSubmissionClicked(MouseEvent event) throws IOException {

        Submission SelectedSubmission = StatusTable.getSelectionModel().getSelectedItem();
        if (SelectedSubmission == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OnlineJudge/Submission/SubmissionShowFXML.fxml"));
        Parent root = loader.load();
        SubmissionShowFXMLController controller = loader.getController();
        controller.setSubmission(SelectedSubmission);
        //OnlineJudge.Nodes.getChildren().removeAll(OnlineJudge.Nodes.getChildren());
        //OnlineJudge.Nodes.getChildren().add(root);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.show();

    }
    

    private void task() {

        Task< Void> tsk = new Task< Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                     StatusTable.refresh();
                     
                    //System.out.println("updating ");
                }
            }

        };
        Thread t = new Thread(tsk);
        t.setDaemon(true);
        t.start();
    }

    
}
