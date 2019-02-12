/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.ProblemSet.ProblemShowFXMLController;
import OnlineJudge.*;
import OnlineJudge.ProblemSet.Problem;
import OnlineJudge.ProblemSet.ProblemSet;
import OnlineJudge.Submission.Submission;
import OnlineJudge.Submission.SubmissionSet;
import Server.ProcessExecutor;
import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.omg.CORBA_2_3.portable.InputStream;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class SubmitFXMLController implements Initializable {
    private Problem problem;
    private final String CppCode = "#include <iostream>\n"
            + "\n"
            + "using namespace std;\n"
            + "\n"
            + "int main() {\n"
            + "	\n"
            + "	return 0;\n"
            + "}";
    private final String JavaCode = "public class Solution {\n"
            + "	public static void main(String[] args) {\n"
            + "		\n"
            + "	}\n"
            + "}";
    @FXML
    private TextField ProblemName;
    @FXML
    private MenuButton SelectLanguageButton;
    @FXML
    private Button ChoseFileButton;
    @FXML
    private Label FileMessege;
    @FXML
    private TextArea SourceCode;
    @FXML
    private Label ErrorMessage;
    @FXML
    private Label ProblemIdMessage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ProblemIdMessage.setVisible(true);
        ProblemName.setEditable(true);
        ErrorMessage.setText("");
    }

    @FXML
    private void CppChosed(ActionEvent event) {
        SelectLanguageButton.setText("C++");
        SourceCode.setText(CppCode);

    }

    @FXML
    private void JavaChoosed(ActionEvent event) {
        SelectLanguageButton.setText("Java");
        SourceCode.setText(JavaCode);
    }

    @FXML
    private void SubmitButtonClicked(ActionEvent event) {
        System.out.println("Submit button clicked");
        if (ProblemName.getText().equals("")) {
            ErrorMessage.setText("Select Problem First");
            return;
        }
        if (!SelectLanguageButton.getText().equals("C++") && !SelectLanguageButton.getText().equals("Java")) {
            ErrorMessage.setText("Select Language First");
            return;
        }
        System.out.println("Submit success");
        ErrorMessage.setText("Submission successful");
        if (!ProblemIdMessage.isVisible() || ProblemSet.Problems.containsKey(ProblemName.getText())) {
            Submission ns =  new Submission(ProblemIdMessage.isVisible() ? ProblemName.getText() : problem.getId(), LocalUser.getUser().getHandle(), SelectLanguageButton.getText(), SourceCode.getText(), SubmissionSet.TotalSubmissions++);
            SubmissionSet.Submissions.put(ns.getId(), ns);
            new ProcessExecutor(ns);
        } else {
            System.out.println("no problem found");
            ErrorMessage.setText("No Problem Found!");
        }

    }

    private String ReadFromFile(File f) {
        String src = "";
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bir = new BufferedInputStream(fis);
            int c = 1;
            while ((c = bir.read()) != -1) {

                System.out.print((char) c);
                src += Character.toString((char) c);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return src;
    }

    @FXML
    private void ChoseFileButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Source Code File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("C++ File", "*.cpp"), new FileChooser.ExtensionFilter("Java File", "*.java"));
        File SourceFile = fileChooser.showOpenDialog(OnlineJudge.PrimaryStage);
        if (SourceFile != null) {
            SourceCode.setText(ReadFromFile(SourceFile));
        } else {
            System.out.println("File not choosen");
        }

    }

    public void setProblem(Problem problem) {
        this.problem= problem;
        ProblemIdMessage.setVisible(false);
        ProblemName.setEditable(false);
        ProblemName.setText(problem.getId() + " - " + problem.getName());
        
    }
    

}
