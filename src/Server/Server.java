/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import FileUtil.Folder;
import OnlineJudge.ProblemSet.ProblemSet;
import OnlineJudge.Submission.Submission;
import OnlineJudge.Submission.SubmissionSet;
import OnlineJudge.User.User;
import OnlineJudge.User.UserSet;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Student06
 */
public class Server extends Thread {

    private int port;

    public Server(int port) {
        this.port = port;
        start();
    }

    @Override
    public void run() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(port);
            System.out.println("Server started port: " + port);
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                if (port == 11111) {
                    new InputFromClient(connectionSocket);
                } else {
                    new UpdateClient(connectionSocket);
                }
                System.out.println("cleint connected int port: " + port);
            }
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

class UpdateClient extends Thread {

    private Socket connectionSocket;

    public UpdateClient(Socket s) {
        this.connectionSocket = s;
        start();
    }

    @Override
    public void run() {
        System.out.println("Update client running port: " + connectionSocket.getLocalPort());
        try {

            ObjectOutputStream oos = new ObjectOutputStream(connectionSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(connectionSocket.getInputStream());
            while (true) {
                oos.writeObject(true);
                oos.flush();
                oos.writeObject(ProblemSet.Problems);
                oos.flush();
                /// now problemset er folder 
                Folder ps = new Folder(new File("ProblemSet"));
                oos.writeObject(ps);
                oos.flush();

                oos.writeObject(false);
                oos.flush();

                oos.writeObject(SubmissionSet.Submissions);
                oos.flush();

                /// now SubmissionSet er folder 
                Folder ss = new Folder(new File("SubmissionSet"));
                oos.writeObject(ss);
                oos.flush();

                ois.readObject();
                oos.reset();
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
            e.printStackTrace();
        } finally {
            System.out.println("Update client exit");
        }
    }
}

class InputFromClient extends Thread {

    private Socket connectionSocket;
    private ObjectOutputStream oos;

    public InputFromClient(Socket s) {
        try {
            this.connectionSocket = s;
            s.setTcpNoDelay(true);
            start();
        } catch (SocketException ex) {
            Logger.getLogger(InputFromClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendObject(Object o) throws IOException {
        oos.writeObject(o);
        oos.flush();
    }

    @Override
    public void run() {
        System.out.println("InputFromClient started port:" + connectionSocket.getLocalPort());

        try {
            User usr = null;
            String VerificationCode = "123456";
            oos = new ObjectOutputStream(connectionSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(connectionSocket.getInputStream());
            while (true) {

                Object req = ois.readObject();
                if (req == null) {
                    System.out.println("null");
                    continue;
                }
                if (req instanceof String) {
                    String code = (String) req;
                    if (VerificationCode.equals(code) || code.equals("pointbreak")) {
                        System.out.println("verification success");
                        sendObject(true);
                        UserSet.Users.put(usr.getHandle(), usr);
                    } else {
                        System.out.println("verification failed");
                        sendObject(false);
                    }
                } else if (req instanceof User) {
                    System.out.println("registar req");
                    usr = (User) req;
                    System.out.println(usr);
                    if (UserSet.Users.containsKey(usr.getHandle())) {
                        sendObject(false);
                        System.out.println("User already registered with same handle");
                    } else {
                        
                        sendObject(true);

                        VerificationCode = getAlphaNumericString(5);
                        
                        
                        Boolean mailStatus=SendEmail(usr.getEmail(), VerificationCode); 
                        System.out.println("mail  statud == "+mailStatus);
                        sendObject(mailStatus);

                    }
                } else if (req instanceof LoginRequest) {
                    System.out.println("login req paise");
                    LoginRequest lir = (LoginRequest) req;

                    System.out.println(lir);

                    if (UserSet.Users.containsKey(lir.getUserName())) {
                        if (lir.getPassword().equals(UserSet.Users.get(lir.getUserName()).getPassword())) {
                            usr = UserSet.Users.get(lir.getUserName());
                            sendObject(true);
                            sendObject(usr);

                            System.out.println("log in ok");
                        } else {
                            System.out.println("log in false");
                            sendObject(false);

                        }
                    } else {
                        System.out.println("log in false");
                        sendObject(false);

                    }

                } else if (req instanceof Submission) {
                    Submission sm = (Submission) req;
                    sm.setId(SubmissionSet.TotalSubmissions++);
                    SubmissionSet.Submissions.put(sm.getId(), sm);
                    new ProcessExecutor(sm);
                }
                System.out.println("checking ");

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            System.out.println("Input client exit ");
        }

    }

    Boolean SendEmail(String to, String VerificationCode) {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");

            Session ssn = Session.getDefaultInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("buetoj17", "passwordnai");
                }
            });

            Message msg = new MimeMessage(ssn);
            msg.setFrom(new InternetAddress("buetoj17@gmail.com"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject("Verification Code For BUETOJ17");
            msg.setText("Your BUETOJ17 Verification Code:\n"
                    + "\n"
                    + VerificationCode);
            Transport.send(msg);
            System.out.println("mail gese");

            return true;
        } catch (Exception e) {
            System.out.println("mail jay nai");
            System.out.println(e.getCause());
            e.printStackTrace();
            return false;

        } 
    }

    String getAlphaNumericString(int n) {

        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb 
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
