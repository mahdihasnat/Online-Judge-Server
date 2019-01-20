/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import OnlineJudge.ProblemSet.ProblemSet;
import OnlineJudge.Submission.Submission;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import OnlineJudge.ProblemSet.*;
import static java.lang.Long.max;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Student06
 */
public class ProcessExecutor {

    int ExitValue;

    public ProcessExecutor(Submission submission) {
        try {
            System.out.println("Exexuting ");

            File SourceCode =null;
            if(submission.Language.equalsIgnoreCase("C++")) 
                SourceCode=new File("SourceCode.cpp");
            else 
                SourceCode = new File("Solution.java");
            if (SourceCode.exists()) {
                SourceCode.delete();
            }
            SourceCode.createNewFile();
            WriteFile(submission.Code, SourceCode.getName());

            File Output = new File("Output.txt");
            if (Output.exists()) {
                Output.delete();
            }
            Output.createNewFile();

            Problem problem = ProblemSet.Problems.get(submission.ProbmemId);
            submission.Verdict = "Judging ... ... ...";
            String Verdict = "";
            for (int i = 0; i < problem.Inputs.size(); i++) {
                submission.Verdict = "Running on test " + i;
                Verdict = ExecuteOneCpp(SourceCode, problem.Inputs.get(i), problem.Outputs.get(i), Output, submission.Comment, problem.TimeLimit, submission.TimeTaken, submission.Language);
                if (!Verdict.equalsIgnoreCase("Accepted")) {
                    break;
                }
            }
            submission.Verdict = Verdict;

            System.out.println(submission);

        } catch (Exception ex) {
            System.out.println(ex.getCause());
            Logger.getLogger(ProcessExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void WriteFile(String Code, String FileName) throws FileNotFoundException {
        System.out.println("In write file");
        System.out.println(Code);
        System.out.println(FileName);
        PrintWriter out = new PrintWriter(FileName);
        out.println(Code);
        out.close();
    }

    static private String ReadFile(File f) {
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

    static String ExecuteOneCpp(File SourceCode, File Input, File Output, File ReirectOutput, String Error, Integer TimeLimit, String TimeTaken, String Language) throws FileNotFoundException, IOException, InterruptedException {

        ProcessBuilder cmd = new ProcessBuilder("cmd");

        // take all commands as input in a text file 
        File CmdCpp = new File("Cmd.txt");
        if (!CmdCpp.exists()) {
            CmdCpp.createNewFile();
        }
        System.out.println("Compiling " + SourceCode.getName());
        if (Language.equalsIgnoreCase("C++")) {
            WriteFile("g++ " + SourceCode.getName(), CmdCpp.getName());
        } else {
            WriteFile("javac " + SourceCode.getName(), CmdCpp.getName());
        }
        File CmdError = new File("CmdError.txt");
        File CmdOutput = new File("CmdOutput.txt");

        // redirect all the files 
        cmd.redirectInput(CmdCpp);
        cmd.redirectOutput(CmdOutput);
        cmd.redirectError(CmdError);

        File exe = new File("");

        if (Language.equalsIgnoreCase("C++")) {
            exe = new File("a.exe");
        } else {
            exe = new File("Solution.class");
        }

        if (exe.exists()) {
            exe.delete();
        }
        // start the process 
        System.out.println("Compiling ");
        Process pc = cmd.start();
        int res = pc.waitFor();
        System.out.println("Compilation ok");

        if (!exe.exists()) {
            Error = ReadFile(CmdError);
            return "Compilation Error";
        }

        ProcessBuilder pb = null;
        if (Language.equalsIgnoreCase("C++")) {
            pb = new ProcessBuilder("a.exe");
        } else {
            
            pb = new ProcessBuilder("cmd");
            File Temp = new File("JavaInput");
        }

        /// pb-> c++ programme
        pb.redirectInput(Input);
        pb.redirectOutput(ReirectOutput);
        pb.redirectError(CmdError);

        long StartTime = System.nanoTime();
        Process pce = pb.start();
        
        boolean finished = pce.waitFor(TimeLimit, TimeUnit.MILLISECONDS);
        System.out.println("programme Finished : " + finished);
        int timelimite = 0;
        if (!finished) {
            timelimite = 1;
            pce.destroy();
        }

        if (TimeTaken.equals("")) {
            TimeTaken = "0";
        }
        long StopTime = System.nanoTime();
        long TimeElapsed = StopTime - StartTime;
        System.out.println("Time taken " + TimeElapsed);
        Long timeTaken = max(Long.parseLong(TimeTaken) * 1000000, TimeElapsed);
        timeTaken = timeTaken / 1000000;
        TimeTaken = String.valueOf(timeTaken);
        int ExitValue = pce.exitValue();
        System.out.println("Exit value " + ExitValue);
        String Verdict = "";
        if (ReadFile(Output).equals(ReadFile(ReirectOutput))) {
            Verdict = "Accepted";
        } else if (timelimite == 1) {
            Verdict = "Time Limit Exceeded";
        } else if (ExitValue != 0) {
            Verdict = "Runtime error";
            Error = ReadFile(CmdError);
        } else {
            Verdict = "Wrong Answer";
        }
        System.out.println("Exit execxute ones");
        return Verdict;
    }

}
