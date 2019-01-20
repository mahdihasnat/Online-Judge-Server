/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

/**
 *
 * @author MAHDI
 */
public class LocalUser {
    public static User user;
    public static void setAdmin()
    {
        System.out.println("Admin set");
        user = new User("Admin","admin","Admin@admin.com","BD","BUET","admin");
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        LocalUser.user = user;
    }
    
    
}
