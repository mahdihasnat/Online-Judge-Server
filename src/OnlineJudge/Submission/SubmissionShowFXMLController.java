/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.Submission;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class SubmissionShowFXMLController implements Initializable {

    private Submission submission;
    @FXML
    private Label SubmisionId;
    @FXML
    private Label Handle;
    @FXML
    private Label ProblemId;
    @FXML
    private Label Language;
    @FXML
    private Label Verdict;
    @FXML
    private Label TimeTaken;
    @FXML
    private Label MemoryTaken;
    @FXML
    private Label Time;
    @FXML
    private TextArea Code;
    @FXML
    private TextArea Comment;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (submission == null) {
            return;
        }
        SubmisionId.setText(submission.getId().toString());
        Handle.setText(submission.getHandle());
        ProblemId.setText(submission.getProbmemId());
        Language.setText(submission.getLanguage());
        Verdict.setText(submission.getVerdict());
        TimeTaken.setText(submission.getTimeTaken());
        MemoryTaken.setText(submission.getMemoryTaken());
        Time.setText(submission.getTime());
        Code.setText(submission.getCode());
        Comment.setText(submission.getComment());
        // TODO
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
        UpdateData();
    }

    void UpdateData() {
        if (submission == null) {
            return;
        }
        SubmisionId.setText(submission.getId().toString());
        Handle.setText(submission.getHandle());
        ProblemId.setText(submission.getProbmemId());
        Language.setText(submission.getLanguage());
        Verdict.setText(submission.getVerdict());
        TimeTaken.setText(submission.getTimeTaken());
        MemoryTaken.setText(submission.getMemoryTaken());
        Time.setText(submission.getTime());
        Code.setText(submission.getCode());
        Comment.setText(submission.getComment());

    }

}
