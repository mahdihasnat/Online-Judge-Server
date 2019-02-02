/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import FileUtil.Folder;
import OnlineJudge.ProblemSet.ProblemSet;
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
import java.util.logging.Level;
import java.util.logging.Logger;

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
            while (true) {
                oos.writeObject(true);
                oos.flush();
                oos.writeObject(ProblemSet.Problems);
                oos.flush();
                /// now problemset er folder 
                Folder ps= new Folder(new File("ProblemSet"));
                oos.writeObject(ps);
                oos.flush();
                
                oos.writeObject(false);
                oos.flush();
                oos.writeObject(SubmissionSet.Submissions);
                oos.flush();
                
                
                /// now SubmissionSet er folder 
                Folder ss= new Folder(new File("SubmissionSet"));
                oos.writeObject(ss);
                oos.flush();
                
                //System.out.println("data sent");
                Thread.sleep(500);
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
    void sendObject(Object o) throws IOException
    {
        oos.writeObject(o); 
        oos.flush();
    }
    @Override
    public void run() {
        System.out.println("InputFromClient started port:" + connectionSocket.getLocalPort());
        
        try {
            oos = new ObjectOutputStream(connectionSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(connectionSocket.getInputStream());
            while (true) {
                
                Object req = ois.readObject();
                if (req == null) {
                    System.out.println("null");
                    continue;
                }
                if (req instanceof User) {
                    System.out.println("registar req");
                    User usr = (User) req;
                    System.out.println(usr);
                    if (UserSet.Users.containsKey(usr.getHandle())) {
                        sendObject(false);
                        System.out.println("User already registered with same handle");
                    } else {
                        sendObject(true);
                        UserSet.Users.put(usr.getHandle(), usr);
                   }
                }
                
                if (req instanceof LoginRequest) {
                    System.out.println("login req paise");
                    LoginRequest lir = (LoginRequest) req;
                    
                    System.out.println(lir);

                    if (UserSet.Users.containsKey(lir.getUserName())) {
                        if (lir.getPassword().equals(UserSet.Users.get(lir.getUserName()).getPassword())) {
                            User usr = UserSet.Users.get(lir.getUserName());
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

                }
                System.out.println("checking ");
                
            }
        } catch (Exception e) {

        }

    }
}
