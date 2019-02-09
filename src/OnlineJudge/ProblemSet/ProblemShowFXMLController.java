/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.ProblemSet;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author MAHDI
 */
public class ProblemShowFXMLController implements Initializable {
    public  static Problem problem;
    
    public static final File path = new File("ProblemSet");
    static final String FileSeparator = System.getProperty("file.separator");
    private File source;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        source= new File(path.getAbsolutePath()+FileSeparator+problem.getId()+FileSeparator+problem.getStatement());
        
        // TODO
    }  
    
    
}
