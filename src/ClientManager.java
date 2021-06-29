import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientManager implements Runnable {
    Socket client;
    String nationalCode_PassWord;
    InputStream inputStream;
    OutputStream outputStream;
    DataInputStream in;
    PrintWriter out;
    public ClientManager(Socket client) {
        this.client = client;
    }
    public void run() {
        try {
            inputStream = client.getInputStream();
            outputStream = client.getOutputStream();
            in = new DataInputStream(inputStream);
            out = new PrintWriter(outputStream, true);
            if(in.readUTF().equals("signUp"))
            {
                signUp();
                if(in.readUTF().equals("createAccount"))
                {
                    createAccount();
                }
                else
                {

                }
            }
            else
            {
                signIn();
                if(in.readUTF().equals("createAccount"))
                {
                    createAccount();
                }
            }

        } catch (Exception e) {}
    }
    public void signUp()
    {
        try {
            String userName = in.readUTF();
            String nationalCode = in.readUTF();
            String phoneNumber = in.readUTF();
            String emailAddress = in.readUTF();
            String passWord = in.readUTF();
            User user = new User(userName,nationalCode,phoneNumber,emailAddress, passWord);
            nationalCode_PassWord = nationalCode + "-" + passWord;
            PrintWriter printWriter;
            File file = new File(nationalCode+"-"+passWord + "/information.txt");
            printWriter = new PrintWriter(file);
            printWriter.println(user.userName);
            printWriter.println(user.nationalCode);
            printWriter.println(user.phoneNumber);
            printWriter.println(user.emailAddress);
            printWriter.println(user.passWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void signIn()
    {
        try {
            String nationalCode = in.readUTF();
            String passWord = in.readUTF();
            nationalCode_PassWord = nationalCode + "-" + passWord;
            File file = new File(nationalCode+"-"+passWord);
            if(file.exists())  out.print(true);
            else out.print(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createAccount()
    {
        try {
            int accountType = in.readInt();//0 -> current account, 1 -> short saving account, 2 -> long saving account, 3 -> flat account
            String passWord = in.readUTF();
            String alias = in.readUTF();
            Account account = new Account(passWord, alias, accountType);
            File file = new File(nationalCode_PassWord + "/" + account.alias + "-" + account.passWord + ".txt");
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(account.alias);
            printWriter.println(account.accountNumber);
            printWriter.println(account.accountType);
            printWriter.println(account.accountBalance);
            printWriter.println(account.passWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void seeingInformationOfAccounts()
    {
        try {
            String passWord = in.readUTF();
            String alias = in.readUTF();
            File file = new File(nationalCode_PassWord + "/" + alias + "-" + passWord + ".txt");
            if(file.exists())
                out.print(file);///////
            else
                out.print("noFile");////////
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}