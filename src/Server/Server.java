/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Student06
 */


public class Server extends Thread {

    public Server() {
        start();
    }

    @Override
    public void run() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(12345);
            System.out.println("Server started");
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                new InputFromClient(connectionSocket);
                System.out.println("cleint connected");
            }
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

class InputFromClient extends Thread{

    private Socket connectionSocket;

    public InputFromClient(Socket s) {
        this.connectionSocket = s;
        start();
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream oos= new ObjectOutputStream(connectionSocket.getOutputStream());
            ObjectInputStream ois=  new ObjectInputStream(connectionSocket.getInputStream());
            while (true) {
                 /// kaj kor
                 if(ois.available()>0)
                 {
                     Object req=ois.readObject();
                     if(req instanceof LoginRequest)
                     {
                         LoginRequest lir=(LoginRequest)req;
                         System.out.println(lir);
                         oos.writeBoolean(true);
                         oos.flush();
                     }
                 }
            }
        } catch (Exception e) {

        }

    }
}