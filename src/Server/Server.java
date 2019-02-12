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
                        
                        
                        Boolean mailStatus=SendEmail(usr.getEmail(), VerificationCode,usr.getHandle()); 
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

    Boolean SendEmail(String to, String VerificationCode,String Handle) {
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
            msg.setContent("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\n" +
"xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" +
"xmlns:w=\"urn:schemas-microsoft-com:office:word\"\n" +
"xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\n" +
"xmlns=\"http://www.w3.org/TR/REC-html40\">\n" +
"\n" +
"<head>\n" +
"<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\n" +
"<meta name=ProgId content=Word.Document>\n" +
"<meta name=Generator content=\"Microsoft Word 15\">\n" +
"<meta name=Originator content=\"Microsoft Word 15\">\n" +
"<link rel=File-List\n" +
"href=\"Welcome%20To%20BUET%20Online%20Judge%2017_files/filelist.xml\">\n" +
"<!--[if gte mso 9]><xml>\n" +
" <o:DocumentProperties>\n" +
"  <o:Author>Mahdi</o:Author>\n" +
"  <o:LastAuthor>Mahdi</o:LastAuthor>\n" +
"  <o:Revision>28</o:Revision>\n" +
"  <o:TotalTime>21</o:TotalTime>\n" +
"  <o:Created>2019-02-12T20:07:00Z</o:Created>\n" +
"  <o:LastSaved>2019-02-12T20:38:00Z</o:LastSaved>\n" +
"  <o:Pages>1</o:Pages>\n" +
"  <o:Words>44</o:Words>\n" +
"  <o:Characters>254</o:Characters>\n" +
"  <o:Company>CyberSpace</o:Company>\n" +
"  <o:Lines>2</o:Lines>\n" +
"  <o:Paragraphs>1</o:Paragraphs>\n" +
"  <o:CharactersWithSpaces>297</o:CharactersWithSpaces>\n" +
"  <o:Version>16.00</o:Version>\n" +
" </o:DocumentProperties>\n" +
" <o:OfficeDocumentSettings>\n" +
"  <o:AllowPNG/>\n" +
" </o:OfficeDocumentSettings>\n" +
"</xml><![endif]-->\n" +
"<link rel=themeData\n" +
"href=\"Welcome%20To%20BUET%20Online%20Judge%2017_files/themedata.thmx\">\n" +
"<link rel=colorSchemeMapping\n" +
"href=\"Welcome%20To%20BUET%20Online%20Judge%2017_files/colorschememapping.xml\">\n" +
"<!--[if gte mso 9]><xml>\n" +
" <w:WordDocument>\n" +
"  <w:SpellingState>Clean</w:SpellingState>\n" +
"  <w:GrammarState>Clean</w:GrammarState>\n" +
"  <w:TrackMoves>false</w:TrackMoves>\n" +
"  <w:TrackFormatting/>\n" +
"  <w:PunctuationKerning/>\n" +
"  <w:ValidateAgainstSchemas/>\n" +
"  <w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid>\n" +
"  <w:IgnoreMixedContent>false</w:IgnoreMixedContent>\n" +
"  <w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText>\n" +
"  <w:DoNotPromoteQF/>\n" +
"  <w:LidThemeOther>EN-US</w:LidThemeOther>\n" +
"  <w:LidThemeAsian>X-NONE</w:LidThemeAsian>\n" +
"  <w:LidThemeComplexScript>BN</w:LidThemeComplexScript>\n" +
"  <w:Compatibility>\n" +
"   <w:BreakWrappedTables/>\n" +
"   <w:SnapToGridInCell/>\n" +
"   <w:WrapTextWithPunct/>\n" +
"   <w:UseAsianBreakRules/>\n" +
"   <w:DontGrowAutofit/>\n" +
"   <w:SplitPgBreakAndParaMark/>\n" +
"   <w:EnableOpenTypeKerning/>\n" +
"   <w:DontFlipMirrorIndents/>\n" +
"   <w:OverrideTableStyleHps/>\n" +
"  </w:Compatibility>\n" +
"  <w:DocumentVariables>\n" +
"   <w:__Grammarly_42____i>H4sIAAAAAAAEAKtWckksSQxILCpxzi/NK1GyMqwFAAEhoTITAAAA</w:__Grammarly_42____i>\n" +
"   <w:__Grammarly_42___1>H4sIAAAAAAAEAKtWcslP9kxRslIyNDa0NDExNzUzNzE3MDU0trBU0lEKTi0uzszPAykwqgUADpZELiwAAAA=</w:__Grammarly_42___1>\n" +
"  </w:DocumentVariables>\n" +
"  <w:BrowserLevel>MicrosoftInternetExplorer4</w:BrowserLevel>\n" +
"  <m:mathPr>\n" +
"   <m:mathFont m:val=\"Cambria Math\"/>\n" +
"   <m:brkBin m:val=\"before\"/>\n" +
"   <m:brkBinSub m:val=\"&#45;-\"/>\n" +
"   <m:smallFrac m:val=\"off\"/>\n" +
"   <m:dispDef/>\n" +
"   <m:lMargin m:val=\"0\"/>\n" +
"   <m:rMargin m:val=\"0\"/>\n" +
"   <m:defJc m:val=\"centerGroup\"/>\n" +
"   <m:wrapIndent m:val=\"1440\"/>\n" +
"   <m:intLim m:val=\"subSup\"/>\n" +
"   <m:naryLim m:val=\"undOvr\"/>\n" +
"  </m:mathPr></w:WordDocument>\n" +
"</xml><![endif]--><!--[if gte mso 9]><xml>\n" +
" <w:LatentStyles DefLockedState=\"false\" DefUnhideWhenUsed=\"false\"\n" +
"  DefSemiHidden=\"false\" DefQFormat=\"false\" DefPriority=\"99\"\n" +
"  LatentStyleCount=\"371\">\n" +
"  <w:LsdException Locked=\"false\" Priority=\"0\" QFormat=\"true\" Name=\"Normal\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 7\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 8\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 9\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 6\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 7\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 8\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index 9\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 7\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 8\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"toc 9\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Normal Indent\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"footnote text\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"annotation text\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"header\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"footer\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"index heading\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"35\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"caption\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"table of figures\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"envelope address\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"envelope return\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"footnote reference\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"annotation reference\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"line number\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"page number\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"endnote reference\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"endnote text\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"table of authorities\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"macro\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"toa heading\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Bullet\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Number\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Bullet 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Bullet 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Bullet 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Bullet 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Number 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Number 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Number 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Number 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"10\" QFormat=\"true\" Name=\"Title\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Closing\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Signature\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"1\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"Default Paragraph Font\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text Indent\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Continue\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Continue 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Continue 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Continue 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"List Continue 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Message Header\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"11\" QFormat=\"true\" Name=\"Subtitle\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Salutation\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Date\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text First Indent\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text First Indent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Note Heading\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text Indent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Body Text Indent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Block Text\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Hyperlink\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"FollowedHyperlink\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"22\" QFormat=\"true\" Name=\"Strong\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"20\" QFormat=\"true\" Name=\"Emphasis\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Document Map\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Plain Text\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"E-mail Signature\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Top of Form\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Bottom of Form\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Normal (Web)\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Acronym\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Address\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Cite\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Code\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Definition\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Keyboard\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Preformatted\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Sample\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Typewriter\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"HTML Variable\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Normal Table\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"annotation subject\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"No List\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Outline List 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Outline List 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Outline List 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Simple 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Simple 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Simple 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Classic 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Classic 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Classic 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Classic 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Colorful 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Colorful 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Colorful 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Columns 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Columns 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Columns 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Columns 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Columns 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 6\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 7\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Grid 8\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 4\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 5\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 6\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 7\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table List 8\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table 3D effects 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table 3D effects 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table 3D effects 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Contemporary\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Elegant\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Professional\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Subtle 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Subtle 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Web 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Web 2\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Web 3\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Balloon Text\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"Table Grid\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\n" +
"   Name=\"Table Theme\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" Name=\"Placeholder Text\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"1\" QFormat=\"true\" Name=\"No Spacing\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" SemiHidden=\"true\" Name=\"Revision\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"34\" QFormat=\"true\"\n" +
"   Name=\"List Paragraph\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"29\" QFormat=\"true\" Name=\"Quote\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"30\" QFormat=\"true\"\n" +
"   Name=\"Intense Quote\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"19\" QFormat=\"true\"\n" +
"   Name=\"Subtle Emphasis\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"21\" QFormat=\"true\"\n" +
"   Name=\"Intense Emphasis\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"31\" QFormat=\"true\"\n" +
"   Name=\"Subtle Reference\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"32\" QFormat=\"true\"\n" +
"   Name=\"Intense Reference\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"33\" QFormat=\"true\" Name=\"Book Title\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"37\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" Name=\"Bibliography\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\n" +
"   UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"TOC Heading\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"41\" Name=\"Plain Table 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"42\" Name=\"Plain Table 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"43\" Name=\"Plain Table 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"44\" Name=\"Plain Table 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"45\" Name=\"Plain Table 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"40\" Name=\"Grid Table Light\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\" Name=\"Grid Table 1 Light\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\" Name=\"Grid Table 6 Colorful\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\" Name=\"Grid Table 7 Colorful\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"Grid Table 1 Light Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"Grid Table 6 Colorful Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"Grid Table 7 Colorful Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"Grid Table 1 Light Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"Grid Table 6 Colorful Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"Grid Table 7 Colorful Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"Grid Table 1 Light Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"Grid Table 6 Colorful Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"Grid Table 7 Colorful Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"Grid Table 1 Light Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"Grid Table 6 Colorful Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"Grid Table 7 Colorful Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"Grid Table 1 Light Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"Grid Table 6 Colorful Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"Grid Table 7 Colorful Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"Grid Table 1 Light Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"Grid Table 6 Colorful Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"Grid Table 7 Colorful Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\" Name=\"List Table 1 Light\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\" Name=\"List Table 6 Colorful\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\" Name=\"List Table 7 Colorful\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"List Table 1 Light Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"List Table 6 Colorful Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"List Table 7 Colorful Accent 1\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"List Table 1 Light Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"List Table 6 Colorful Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"List Table 7 Colorful Accent 2\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"List Table 1 Light Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"List Table 6 Colorful Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"List Table 7 Colorful Accent 3\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"List Table 1 Light Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"List Table 6 Colorful Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"List Table 7 Colorful Accent 4\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"List Table 1 Light Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"List Table 6 Colorful Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"List Table 7 Colorful Accent 5\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"46\"\n" +
"   Name=\"List Table 1 Light Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"51\"\n" +
"   Name=\"List Table 6 Colorful Accent 6\"/>\n" +
"  <w:LsdException Locked=\"false\" Priority=\"52\"\n" +
"   Name=\"List Table 7 Colorful Accent 6\"/>\n" +
" </w:LatentStyles>\n" +
"</xml><![endif]-->\n" +
"<style>\n" +
"<!--\n" +
" /* Font Definitions */\n" +
" @font-face\n" +
"	{font-family:Vrinda;\n" +
"	panose-1:2 11 5 2 4 2 4 2 2 3;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:swiss;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:65539 0 0 0 1 0;}\n" +
"@font-face\n" +
"	{font-family:\"Cambria Math\";\n" +
"	panose-1:2 4 5 3 5 4 6 3 2 4;\n" +
"	mso-font-charset:1;\n" +
"	mso-generic-font-family:roman;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:0 0 0 0 0 0;}\n" +
"@font-face\n" +
"	{font-family:Garamond;\n" +
"	panose-1:2 2 4 4 3 3 1 1 8 3;\n" +
"	mso-font-charset:0;\n" +
"	mso-generic-font-family:roman;\n" +
"	mso-font-pitch:variable;\n" +
"	mso-font-signature:647 0 0 0 159 0;}\n" +
" /* Style Definitions */\n" +
" p.MsoNormal, li.MsoNormal, div.MsoNormal\n" +
"	{mso-style-unhide:no;\n" +
"	mso-style-qformat:yes;\n" +
"	mso-style-parent:\"\";\n" +
"	margin-top:0cm;\n" +
"	margin-right:0cm;\n" +
"	margin-bottom:8.0pt;\n" +
"	margin-left:0cm;\n" +
"	line-height:106%;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:11.0pt;\n" +
"	font-family:\"Garamond\",serif;\n" +
"	mso-ascii-font-family:Garamond;\n" +
"	mso-ascii-theme-font:minor-latin;\n" +
"	mso-fareast-font-family:Garamond;\n" +
"	mso-fareast-theme-font:minor-latin;\n" +
"	mso-hansi-font-family:Garamond;\n" +
"	mso-hansi-theme-font:minor-latin;\n" +
"	mso-bidi-font-family:Vrinda;\n" +
"	mso-bidi-theme-font:minor-bidi;}\n" +
"a:link, span.MsoHyperlink\n" +
"	{mso-style-priority:99;\n" +
"	color:blue;\n" +
"	text-decoration:underline;\n" +
"	text-underline:single;}\n" +
"a:visited, span.MsoHyperlinkFollowed\n" +
"	{mso-style-noshow:yes;\n" +
"	mso-style-priority:99;\n" +
"	color:#85DFD0;\n" +
"	mso-themecolor:followedhyperlink;\n" +
"	text-decoration:underline;\n" +
"	text-underline:single;}\n" +
"p.msonormal0, li.msonormal0, div.msonormal0\n" +
"	{mso-style-name:msonormal;\n" +
"	mso-style-unhide:no;\n" +
"	mso-margin-top-alt:auto;\n" +
"	margin-right:0cm;\n" +
"	mso-margin-bottom-alt:auto;\n" +
"	margin-left:0cm;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:12.0pt;\n" +
"	font-family:\"Times New Roman\",serif;\n" +
"	mso-fareast-font-family:\"Times New Roman\";\n" +
"	mso-fareast-theme-font:minor-fareast;}\n" +
"span.il\n" +
"	{mso-style-name:il;\n" +
"	mso-style-unhide:no;}\n" +
"span.SpellE\n" +
"	{mso-style-name:\"\";\n" +
"	mso-spl-e:yes;}\n" +
".MsoChpDefault\n" +
"	{mso-style-type:export-only;\n" +
"	mso-default-props:yes;\n" +
"	font-size:10.0pt;\n" +
"	mso-ansi-font-size:10.0pt;\n" +
"	mso-bidi-font-size:10.0pt;\n" +
"	font-family:\"Garamond\",serif;\n" +
"	mso-ascii-font-family:Garamond;\n" +
"	mso-ascii-theme-font:minor-latin;\n" +
"	mso-fareast-font-family:Garamond;\n" +
"	mso-fareast-theme-font:minor-latin;\n" +
"	mso-hansi-font-family:Garamond;\n" +
"	mso-hansi-theme-font:minor-latin;\n" +
"	mso-bidi-font-family:Vrinda;\n" +
"	mso-bidi-theme-font:minor-bidi;}\n" +
"@page WordSection1\n" +
"	{size:612.0pt 792.0pt;\n" +
"	margin:72.0pt 72.0pt 72.0pt 72.0pt;\n" +
"	mso-header-margin:35.4pt;\n" +
"	mso-footer-margin:35.4pt;\n" +
"	mso-paper-source:0;}\n" +
"div.WordSection1\n" +
"	{page:WordSection1;}\n" +
"-->\n" +
"</style>\n" +
"<!--[if gte mso 10]>\n" +
"<style>\n" +
" /* Style Definitions */\n" +
" table.MsoNormalTable\n" +
"	{mso-style-name:\"Table Normal\";\n" +
"	mso-tstyle-rowband-size:0;\n" +
"	mso-tstyle-colband-size:0;\n" +
"	mso-style-noshow:yes;\n" +
"	mso-style-priority:99;\n" +
"	mso-style-parent:\"\";\n" +
"	mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n" +
"	mso-para-margin:0cm;\n" +
"	mso-para-margin-bottom:.0001pt;\n" +
"	mso-pagination:widow-orphan;\n" +
"	font-size:10.0pt;\n" +
"	font-family:\"Garamond\",serif;\n" +
"	mso-ascii-font-family:Garamond;\n" +
"	mso-ascii-theme-font:minor-latin;\n" +
"	mso-hansi-font-family:Garamond;\n" +
"	mso-hansi-theme-font:minor-latin;}\n" +
"</style>\n" +
"<![endif]--><!--[if gte mso 9]><xml>\n" +
" <o:shapedefaults v:ext=\"edit\" spidmax=\"1026\"/>\n" +
"</xml><![endif]--><!--[if gte mso 9]><xml>\n" +
" <o:shapelayout v:ext=\"edit\">\n" +
"  <o:idmap v:ext=\"edit\" data=\"1\"/>\n" +
" </o:shapelayout></xml><![endif]-->\n" +
"</head>\n" +
"\n" +
"<body bgcolor=\"#C4EEFF\" lang=EN-US link=blue vlink=\"#85DFD0\" style='tab-interval:\n" +
"36.0pt'>\n" +
"\n" +
"<div class=WordSection1>\n" +
"\n" +
"<p class=MsoNormal style='margin-left:396.0pt;text-indent:36.0pt'><b\n" +
"style='mso-bidi-font-weight:normal'><u><span style='font-size:20.0pt;\n" +
"line-height:106%;color:#0076A3;mso-themecolor:accent2;mso-themeshade:191;\n" +
"mso-style-textfill-fill-color:#0076A3;mso-style-textfill-fill-themecolor:accent2;\n" +
"mso-style-textfill-fill-alpha:100.0%;mso-style-textfill-fill-colortransforms:\n" +
"lumm=75000'>Welcome to BUET Online Judge 17<o:p></o:p></span></u></b></p>\n" +
"\n" +
"<p class=MsoNormal><span style='font-size:14.0pt;line-height:106%'>Your Handle is:\n" +
"<span style='color:#7E9632;mso-themecolor:accent6;mso-themeshade:191;\n" +
"mso-style-textfill-fill-color:#7E9632;mso-style-textfill-fill-themecolor:accent6;\n" +
"mso-style-textfill-fill-alpha:100.0%;mso-style-textfill-fill-colortransforms:\n" +
"lumm=75000'>"+Handle+"</span><o:p></o:p></span></p>\n" +
"\n" +
"<p class=MsoNormal><span style='font-size:14.0pt;line-height:106%'><o:p>&nbsp;</o:p></span></p>\n" +
"\n" +
"<p class=MsoNormal><span style='font-size:14.0pt;line-height:106%'>Your\n" +
"Registration Verification Code is: <span style='color:red'>"+VerificationCode+"</span><o:p></o:p></span></p>\n" +
"\n" +
"<p class=MsoNormal><span style='font-size:14.0pt;line-height:106%'><o:p>&nbsp;</o:p></span></p>\n" +
"\n" +
"<p class=MsoNormal><span style='font-size:14.0pt;line-height:106%'>Regards,<o:p></o:p></span></p>\n" +
"\n" +
"<p class=MsoNormal><span style='font-size:14.0pt;line-height:106%'>BuetOJ17\n" +
"Team, Mahdi &amp; <span class=SpellE>Solaimon</span><o:p></o:p></span></p>\n" +
"\n" +
"<p class=MsoNormal><b style='mso-bidi-font-weight:normal'><u><span\n" +
"style='font-size:14.0pt;line-height:106%'>NOTE:</span></u></b><span\n" +
"style='font-size:14.0pt;line-height:106%'> This email was automatically\n" +
"generated from BUET Online Judge 17 (<a href=\"https://github.com/mahdihasnat\">https://github.com/mahdihasnat</a>).<o:p></o:p></span></p>\n" +
"\n" +
"</div>\n" +
"\n" +
"</body>\n" +
"\n" +
"</html>","text/html");
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
