/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MAHDI
 */
public class LocalUser {
    private static User user;
    public static void setAdmin()
    {
        if(UserSet.Users.containsKey("admin"))
        {
            user = UserSet.Users.get("admin");
            return ;
        }
        try {
            System.out.println("Admin set");
            String address= InetAddress.getLocalHost().getHostAddress();
            
            user = new User(address,"admin","buetoj17@gmail.com","BD","BUET","passwordnai");
            if(!UserSet.Users.containsKey("admin"))
                UserSet.Users.put("admin", user);
        } catch (UnknownHostException ex) {
            Logger.getLogger(LocalUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static User getUser() {
        if(user==null) setAdmin();
        return UserSet.Users.get("admin");
    }

    
    
    
}
