import java.io.File;

public class User {
    public String userName;
    public String nationalCode;
    public String phoneNumber;
    public String emailAddress;
    public String passWord;
    File file;
    public User(String userName,String nationalCode,String phoneNumber,String emailAddress, String passWord)
    {
        this.userName = userName;
        this.nationalCode = nationalCode;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.passWord = passWord;
        file = new File("src/" +nationalCode+"-"+passWord);
        file.mkdir();
    }
}
