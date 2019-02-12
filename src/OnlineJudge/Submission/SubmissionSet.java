/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.Submission;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author MAHDI
 */
public class SubmissionSet {
    public static HashMap< Integer , Submission> Submissions = new HashMap< Integer, Submission>();
    static final File path=new File("SubmissionSet");
    static final String FileSeparator=System.getProperty("file.separator");
    public static Integer TotalSubmissions = new Integer(0);
    public static void SaveSubmissionSet() {
        try {
            System.out.println("save Submission");
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    System.out.println("Submissionse dir not created");
                }
            }
            File Dest = new File(path.getAbsolutePath()+FileSeparator+"Submissions.dat");
            FileOutputStream fos = new FileOutputStream(Dest);
            ObjectOutputStream ous = new ObjectOutputStream(fos);
            ous.writeObject(Submissions);
            ous.writeObject(TotalSubmissions);
            
            ous.close();
            fos.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
            e.printStackTrace();
        }
        
    }
    public static void LoadSubmissionSet()
    {
        try {
            System.out.println("load Submission");
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    System.out.println("Submissionse dir not created");
                }
            }
            File Dest = new File(path.getAbsolutePath()+FileSeparator+"Submissions.dat");
            if(!Dest.exists()) return ;
            FileInputStream fis = new FileInputStream(Dest);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Submissions= (HashMap< Integer, Submission>)ois.readObject();
            TotalSubmissions=(Integer)ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public static HashMap<Integer, Submission> getSubmissions() {
        return Submissions;
    }

    public static void setSubmissions(HashMap<Integer, Submission> Submissions) {
        SubmissionSet.Submissions = Submissions;
    }

    public static Integer getTotalSubmissions() {
        return TotalSubmissions;
    }

    public static void setTotalSubmissions(Integer TotalSubmissions) {
        SubmissionSet.TotalSubmissions = TotalSubmissions;
    }

    
}
