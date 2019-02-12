/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.ProblemSet;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class DeleteProblemFXMLController implements Initializable {

    @FXML
    private TableView<Problem> ProblemsTable;
    @FXML
    private TableColumn<Problem, String> ProblemId;
    @FXML
    private TableColumn<Problem, String> ProblemName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //System.out.println("Table View ini");
        //ProblemsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ObservableList<Problem> data = FXCollections.observableArrayList();
        //ProblemSet.LoadProblemSet();
        data.addAll(ProblemSet.Problems.values());

        ProblemId.setCellValueFactory(new PropertyValueFactory<Problem, String>("Id"));
        ProblemName.setCellValueFactory(new PropertyValueFactory<Problem, String>("Name"));
        ProblemsTable.setItems(data);

    }

    /*
    @FXML
    private void ShowProblem(KeyEvent event) {
        System.out.println("ShowProblem in keyboard ");
        }
     */
    @FXML
    private void ShowProblem(MouseEvent event) {
        Problem SelectedProblem = ProblemsTable.getSelectionModel().getSelectedItem();
        System.out.println("Selected problm " + SelectedProblem);
        if (ProblemSet.Problems.containsKey(SelectedProblem.getId())) {
            ProblemSet.Problems.remove(SelectedProblem.getId());

            ObservableList<Problem> data = FXCollections.observableArrayList();
            //ProblemSet.LoadProblemSet();
            data.addAll(ProblemSet.Problems.values());

            ProblemId.setCellValueFactory(new PropertyValueFactory<Problem, String>("Id"));
            ProblemName.setCellValueFactory(new PropertyValueFactory<Problem, String>("Name"));
            ProblemsTable.setItems(data);

        }

    }

    @FXML
    private void ShowProblem(KeyEvent event) {
        
    }

}
