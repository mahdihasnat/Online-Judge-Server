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
import OnlineJudge.User.LocalUser;
import OnlineJudge.User.User;
import OnlineJudge.User.UserSet;
import static java.lang.Long.max;
import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
/**
 *
 * @author Student06
 */
public class ProcessExecutor extends Thread {

    Submission submission;
    Problem problem;
    User user;
    int ExitValue;
    static final String FileSeparator = System.getProperty("file.separator");

    public ProcessExecutor(Submission submission) {
        this.submission = submission;
        problem = ProblemSet.Problems.get(submission.getProbmemId());
        user = UserSet.Users.get(submission.getHandle());
        user.getMySubmissions().add(submission.getId());
        submission.setVerdict("Waiting for judjing");
        start();
    }

    static void WriteFile(String Code, String FileName) throws FileNotFoundException {

        File f = new File(FileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ProcessExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //System.out.println("In write file");
        //System.out.println(Code);
        //System.out.println(FileName);
        PrintWriter out = new PrintWriter(FileName);
        out.println(Code);
        out.close();
    }

    static private String ReadFile(File f) {
        String src = "";
        try {
            
            
            /*
            try {
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bir = new BufferedInputStream(fis);
            int c = 1;
            while ((c = bir.read()) != -1) {
            
            //System.out.print((char) c);
            src += Character.toString((char) c);
            }
            } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            }
            */
            byte [] fileBytes = Files.readAllBytes(f.toPath());
            src = new String (fileBytes);
            return src;
        } catch (IOException ex) {
            Logger.getLogger(ProcessExecutor.class.getName()).log(Level.SEVERE, null, ex);
            return src;
        }
    }
    @Override
     public void run() {


            try {
                //System.out.println("Exexuting ");

                problem.IncreamentTotalAttempted();

                File SourceCode = null;
                if (submission.getLanguage().equalsIgnoreCase("C++")) {
                    SourceCode = new File("SourceCode.cpp");

                    if (SourceCode.exists()) {
                        SourceCode.delete();
                    }
                    SourceCode.createNewFile();
                    WriteFile(submission.getCode(), SourceCode.getName());

                    File Output = new File("Output.txt");
                    if (Output.exists()) {
                        Output.delete();
                    }
                    Output.createNewFile();

                    submission.setVerdict("Judging ... ... ...");
                    String Verdict = "";

                    String Path_plus_separator = Problem.path.getAbsolutePath() + FileSeparator + problem.getId() + FileSeparator;
                    for (int i = 1; i <= problem.getTotalInputs(); i++) {
                        submission.setVerdict( "Running on test " + i);
                        Verdict = RunCpp(submission, problem, new File(Path_plus_separator + "Input" + i + ".txt"), new File(Path_plus_separator + "Output" + i + ".txt"), SourceCode, Output);
                        if (!Verdict.equalsIgnoreCase("Accepted")) {
                            break;
                        }
                    }
                    submission.setVerdict ( Verdict);
                    //System.out.println(submission);

                } else {

                    SourceCode = new File("Solution.java");

                    if (SourceCode.exists()) {
                        SourceCode.delete();
                    }
                    SourceCode.createNewFile();
                    WriteFile(submission.getCode(), SourceCode.getName());

                    File Output = new File("Output.txt");
                    if (Output.exists()) {
                        Output.delete();
                    }
                    Output.createNewFile();

                    submission.setVerdict ( "Judging ... ... ...");
                    String Verdict = "";

                    String Path_plus_separator = Problem.path.getAbsolutePath() + FileSeparator + problem.getId() + FileSeparator;
                    for (int i = 1; i <= problem.getTotalInputs(); i++) {
                        submission.setVerdict ( "Running on test " + i);
                        Verdict = RunJava(submission, problem, new File(Path_plus_separator + "Input" + i + ".txt"), new File(Path_plus_separator + "Output" + i + ".txt"), SourceCode, Output);
                        if (!Verdict.equalsIgnoreCase("Accepted")) {
                            break;
                        }
                    }
                    submission.setVerdict ( Verdict);
                    
                }
                
                System.out.println(submission);
                
                if (submission.getVerdict().equalsIgnoreCase("Accepted")) {
                    problem.IncreamentTotalAccepted();
                }


            } catch (Exception ex) {
                System.out.println(ex.getCause());
                Logger.getLogger(ProcessExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }

    public static synchronized String RunCpp(Submission submission, Problem problem, File Input, File Output, File SourceCode, File ReirectOutput) throws Exception {
        ProcessBuilder cmd = new ProcessBuilder("cmd");

        // take all commands as input in a text file 
        File CmdCpp = new File("Cmd.txt");
        
        System.out.println("Compiling " + SourceCode.getName());

        WriteFile("g++ " + SourceCode.getName(), CmdCpp.getName());

        File CmdError = new File("CmdError.txt");
        File CmdOutput = new File("CmdOutput.txt");

        // redirect all the files 
        cmd.redirectInput(CmdCpp);
        cmd.redirectOutput(CmdOutput);
        cmd.redirectError(CmdError);

        File exe = new File("a.exe");
        if (exe.exists()) {
            exe.delete();
        }

        // start the process 
        System.out.println("Compiling ");
        Process pc = cmd.start();
        int res = pc.waitFor();
        System.out.println("Compilation ok");

        if (!exe.exists()) {
            submission.setComment ( ReadFile(CmdError));
            return "Compilation Error";
        }

        ProcessBuilder pb = new ProcessBuilder("a.exe");

        /// pb-> c++ programme
        pb.redirectInput(Input);
        pb.redirectOutput(ReirectOutput);
        pb.redirectError(CmdError);

        long StartTime = System.nanoTime();
        Process pce = pb.start();

        boolean finished = pce.waitFor(problem.getTimeLimit().longValue(), TimeUnit.MILLISECONDS);
        System.out.println("programme Finished : " + finished);
        int timelimite = 0;
        if (!finished) {
            timelimite = 1;
            pce.destroy();
        }
        if (submission.getTimeTaken().equals("")) {
            submission.setTimeTaken ( "0");
        }

        long StopTime = System.nanoTime();
        long TimeElapsed = StopTime - StartTime;
        System.out.println("Time taken " + TimeElapsed + " nano sec");
        Long timeTaken = max(Long.parseLong(submission.getTimeTaken()) * 1000000, TimeElapsed);
        timeTaken = timeTaken / 1000000;
        submission.setTimeTaken ( String.valueOf(timeTaken) );
        int ExitValue = pce.exitValue();
        System.out.println("Exit value " + ExitValue);
        String Verdict = "";
        String myout= ReadFile(ReirectOutput);
        String out = ReadFile(Output);
        if (out.equals(myout)) {
            Verdict = "Accepted";
        } else if (timelimite == 1) {
            Verdict = "Time Limit Exceeded";
        } else if (ExitValue != 0) {
            Verdict = "Runtime error";
            submission.setComment ( ReadFile(CmdError));
        } else {
            Verdict = "Wrong Answer";
        }
        System.out.println("Exit Runcpp ");
        return Verdict;
    }
    private static final String root = new File(new File("1").getAbsoluteFile().getParent()).getAbsolutePath();

    public static synchronized String RunJava(Submission submission, Problem problem, File Input, File Output, File SourceCode, File ReirectOutput) throws Exception {
        ProcessBuilder cmd = new ProcessBuilder("cmd");

        // take all commands as input in a text file 
        File CmdJava = new File("CmdJava.txt");
        if (!CmdJava.exists()) {
            CmdJava.createNewFile();
        }
        //System.out.println("Compiling " + SourceCode.getName());

        WriteFile("javac Solution.java", CmdJava.getName());

        File CmdError = new File("CmdJavaError.txt");
        File CmdOutput = new File("CmdJavaOutput.txt");

        // redirect all the files 
        cmd.redirectInput(CmdJava);
        cmd.redirectOutput(CmdOutput);
        cmd.redirectError(CmdError);

        File cls = new File("Solution.class");
        if (cls.exists()) {
            cls.delete();
        }

        // start the process 
        //System.out.println("Compiling ");
        Process pc = cmd.start();
        int res = pc.waitFor();
        //System.out.println("Compilation ok");

        if (!cls.exists()) {
            submission.setComment ( ReadFile(CmdError));
            return "Compilation Error";
        }

        ProcessBuilder pb = new ProcessBuilder("java",
                "-cp", root,
                "-Xbootclasspath/p:" + root,
                "Solution");

        /// pb-> c++ programme
        pb.redirectInput(Input);
        pb.redirectOutput(ReirectOutput);
        pb.redirectError(CmdError);

        long StartTime = System.nanoTime();
        Process pce = pb.start();

        boolean finished = pce.waitFor(problem.getTimeLimit().longValue(), TimeUnit.MILLISECONDS);
        //System.out.println("programme Finished : " + finished);
        int timelimite = 0;
        if (!finished) {
            timelimite = 1;
            pce.destroy();
        }
        if (submission.getTimeTaken().equals("")) {
            submission.setTimeTaken ( "0");
        }

        long StopTime = System.nanoTime();
        long TimeElapsed = StopTime - StartTime;
        //System.out.println("Time taken " + TimeElapsed + " nano sec");
        Long timeTaken = max(Long.parseLong(submission.getTimeTaken()) * 1000000, TimeElapsed);
        timeTaken = timeTaken / 1000000;
        submission.setTimeTaken ( String.valueOf(timeTaken));
        int ExitValue = pce.exitValue();
        //System.out.println("Exit value " + ExitValue);
        String Verdict = "";
        if (ReadFile(Output).equals(ReadFile(ReirectOutput))) {
            Verdict = "Accepted";
        } else if (timelimite == 1) {
            Verdict = "Time Limit Exceeded";
        } else if (ExitValue != 0) {
            Verdict = "Runtime error";
            submission.setComment ( ReadFile(CmdError));
        } else {
            Verdict = "Wrong Answer";
        }
        System.out.println("Exit runjava");
        return Verdict;
    }

}
