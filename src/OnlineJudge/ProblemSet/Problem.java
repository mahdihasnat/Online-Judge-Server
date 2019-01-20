/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.ProblemSet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.objects.NativeRegExp.source;

/**
 *
 * @author MAHDI
 */
public class Problem implements Serializable{
    private String Id;
    private File Statement;// pdf type statement 
    private String Type;// "static" ,"dynamic","interactive"
    private File VerifierCode;// if dynamic then verifier cpp 
    private ArrayList< File > Inputs;
    private ArrayList< File > Outputs;
    private int TotalAccepted;
    private int TotalAttempted;
    private String Name;
    private Integer TimeLimit;/// always millisec
    private Integer MemoryLimit;
    public Problem(String Id,String Name )
    {
        this.Id=Id;
        this.Name=Name;
    }


    @Override
    public String toString() {
        return "Problem{" + "Id=" + Id + ", Statement=" + Statement + ", Type=" + Type + ", VerifierCode=" + VerifierCode + ", Inputs=" + Inputs + ", Outputs=" + Outputs + ", TotalAccepted=" + TotalAccepted + ", TotalAttempted=" + TotalAttempted + ", Name=" + Name + ", TimeLimit=" + TimeLimit + ", MemoryLimit=" + MemoryLimit + '}';
    }
    static final File path=new File("ProblemSet");
    static final String FileSeparator=System.getProperty("file.separator");
    public Problem(String Id, File Statement, String Type, File VerifierCode, ArrayList<File> Inputs, String Name, Integer TimeLimit, Integer MemoryLimit)  {
        try {
        System.out.println("Problem constructor");
        if(!path.exists()) 
        {
            if(!path.mkdirs())
            {
                System.out.println("Problemse dir not created");
            }
        }
        File Path=new File(path.getAbsolutePath()+FileSeparator+Id);
        if(!Path.exists()) 
        {
            if(!Path.mkdirs())
            {
                System.out.println("Problems folder dir not created");
            }
        }
        this.Id = Id;
        this.Statement=CopyFiles(Statement,"Statement",Path);
        System.out.println("Statement copied");
        this.Type = Type;
        
        this.VerifierCode=CopyFiles(VerifierCode,"VerifierCode",Path);
        this.Inputs= new ArrayList<File>();
        int n=1;
        for(File f:Inputs)
        {
            this.Inputs.add(CopyFiles(VerifierCode,"Input"+(n++),Path));
        }
        
        this.Name = Name;
        this.TimeLimit = TimeLimit;
        this.MemoryLimit = MemoryLimit;
        }
        catch(Exception e)
        {
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    public Problem(String Id, File Statement, String Type, ArrayList<File> Inputs, ArrayList<File> Outputs, String Name, Integer TimeLimit, Integer MemoryLimit) {
        try {
        System.out.println("Problem constructor");
        if(!path.exists()) 
        {
            if(!path.mkdirs())
            {
                System.out.println("Problemse dir not created");
            }
        }
        File Path=new File(path.getAbsolutePath()+FileSeparator+Id);
        if(!Path.exists()) 
        {
            if(!Path.mkdirs())
            {
                System.out.println("Problems folder dir not created");
            }
        }
        this.Id = Id;
        this.Statement=CopyFiles(Statement,"Statement",Path);
        System.out.println("Statement copied");
        this.Type = Type;
        
        this.Inputs= new ArrayList<File>();
        int n=1;
        for(File f:Inputs)
        {
            this.Inputs.add(CopyFiles(f,"Input"+(n++),Path));
        }
        
        this.Outputs= new ArrayList<File>();
         n=1;
        for(File f:Outputs)
        {
            this.Outputs.add(CopyFiles(f,"Output"+(n++),Path));
        }
        
        this.Name = Name;
        this.TimeLimit = TimeLimit;
        this.MemoryLimit = MemoryLimit;
        }
        catch(Exception e)
        {
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }
    static File CopyFiles(File src,String field,File Path)
    {
        try {
            File dest = new File(Path.getAbsolutePath()+FileSeparator+field+GetExtension(src.getAbsolutePath()));
            if(!dest.exists())
                dest.createNewFile();
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File created: "+dest.getAbsolutePath());
            return dest;
        } catch (Exception ex) {
            System.out.println(ex.getCause());
            }
       
        return null;
    }
    
    static String GetExtension(String s)
    {
        int id=s.lastIndexOf('.');
        String ex="";
        for(int i=id;i<s.length();i++)
            ex+=s.charAt(i);
        return ex;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public File getStatement() {
        return Statement;
    }

    public void setStatement(File Statement) {
        this.Statement = Statement;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public File getVerifierCode() {
        return VerifierCode;
    }

    public void setVerifierCode(File VerifierCode) {
        this.VerifierCode = VerifierCode;
    }

    public ArrayList< File > getInputs() {
        return Inputs;
    }

    public void setInputs(ArrayList< File > Inputs) {
        this.Inputs = Inputs;
    }

    public ArrayList< File > getOutputs() {
        return Outputs;
    }

    public void setOutputs(ArrayList< File > Outputs) {
        this.Outputs = Outputs;
    }

    public int getTotalAccepted() {
        return TotalAccepted;
    }

    public void setTotalAccepted(int TotalAccepted) {
        this.TotalAccepted = TotalAccepted;
    }

    public int getTotalAttempted() {
        return TotalAttempted;
    }

    public void setTotalAttempted(int TotalAttempted) {
        this.TotalAttempted = TotalAttempted;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Integer getTimeLimit() {
        return TimeLimit;
    }

    public void setTimeLimit(Integer TimeLimit) {
        this.TimeLimit = TimeLimit;
    }

    public Integer getMemoryLimit() {
        return MemoryLimit;
    }

    public void setMemoryLimit(Integer MemoryLimit) {
        this.MemoryLimit = MemoryLimit;
    }
}
