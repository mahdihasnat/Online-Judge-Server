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
    public String Id;
    public File Statement;// pdf type statement 
    public String Type;// "static" ,"dynamic","interactive"
    public File VerifierCode;// if dynamic then verifier cpp 
    public ArrayList< File > Inputs;
    public ArrayList< File > Outputs;
    public int TotalAccepted;
    public int TotalAttempted;
    public String Name;
    public Integer TimeLimit;/// always millisec
    public Integer MemoryLimit;
    public Problem(String Id,String Name )
    {
        this.Id=Id;
        this.Name=Name;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
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
    
    
    
}
