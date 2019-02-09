/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OnlineJudge.User;

import OnlineJudge.Submission.Submission;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author MAHDI
 */
public class User implements Serializable{
    private String Name;
    private String Handle;
    private String Email;
    private String Country;
    private String University;
    private String Password;
    transient Thread myThread;
    private ArrayList< Integer > mySubmissions; 
    public User(String Name, String Handle, String Email, String Country, String University, String Password) {
        if(Country.equals("")) Country="Bangladesh";
        if(University.equals("")) University="BUET";
        this.Name = Name;
        this.Handle = Handle;
        this.Email = Email;
        this.Country = Country;
        this.University = University;
        this.Password = Password;
    }
    public User(String Name)
    {
        this.Name = Name;
    }
    
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getHandle() {
        return Handle;
    }

    public void setHandle(String Handle) {
        this.Handle = Handle;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public String getUniversity() {
        return University;
    }

    public void setUniversity(String University) {
        this.University = University;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public ArrayList<Integer> getMySubmissions() {
        return mySubmissions;
    }

    public void setMySubmissions(ArrayList<Integer> mySubmissions) {
        this.mySubmissions = mySubmissions;
    }
    
    
    @Override
    public String toString() {
        return "User{" + "Name=" + Name + ", Handle=" + Handle + ", Email=" + Email + ", Country=" + Country + ", University=" + University + ", Password=" + Password + ", myThread=" + myThread + '}';
    }
    
}
