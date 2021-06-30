import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientManager implements Runnable {
    Socket client;
    String nationalCode_PassWord;
    InputStream inputStream;
    OutputStream outputStream;
    DataInputStream in;
    DataOutputStream out;
    public ClientManager(Socket client) {
        this.client = client;
    }
    public void run() {
        try {
            inputStream = client.getInputStream();
            outputStream = client.getOutputStream();
            in = new DataInputStream(inputStream);
            out = new DataOutputStream(outputStream);
            if(in.readUTF().equals("signUp"))
            {
                System.out.println("signUp");
                signUp();
                if(in.readUTF().equals("createAccount"))
                {
                    System.out.println("createAccount");
                    createAccount();
                }
                else
                {
                    //System.out.println("back");
                }
            }
            else
            {
                System.out.println("signIn");
                signIn();
                if(in.readUTF().equals("LogIn"))
                {
                    logIn();
                    if (in.readUTF().equals("enter"))
                    {
                        //enter();
                    }
                    else
                        System.out.println("back");
                }
                else
                {
                    //System.out.println("back");
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
            File file = new File("src/" + nationalCode + "-" + passWord + "/information.txt");
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(userName);
            printWriter.println(nationalCode);
            printWriter.println(phoneNumber);
            printWriter.println(emailAddress);
            printWriter.println(passWord);
            printWriter.close();
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
            File file = new File("src/" + nationalCode+"-"+passWord);
            if(file.exists())
                out.writeBoolean(true);
            else
                out.writeBoolean(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createAccount()
    {
        try {
            String accountType = in.readUTF();//0 -> current account, 1 -> short saving account, 2 -> long saving account, 3 -> flat account
            String passWord = in.readUTF();
            String alias = in.readUTF();
            Account account = new Account(passWord, alias, accountType);
            File file = new File("src/" + nationalCode_PassWord + "/" + account.alias + "-" + account.passWord + ".txt");
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(account.alias);
            printWriter.println(account.accountNumber);
            printWriter.println(account.accountType);
            printWriter.println(account.accountBalance);
            printWriter.println(account.passWord);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logIn()
    {
        try {
            String alias = in.readUTF();
            String passWord = in.readUTF();
            File file = new File("src/" + nationalCode_PassWord + "/" + alias + "-" + passWord + ".txt");
            if(file.exists())
                out.writeBoolean(true);
            else
                out.writeBoolean(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*public void seeingInformationOfAccounts()
    {
        try {
            String passWord = in.readUTF();
            String alias = in.readUTF();
            File file = new File(nationalCode_PassWord + "/" + alias + "-" + passWord + ".txt");
            if(file.exists())
                out.writeUTF(file);///////
            else
                out.print("noFile");////////
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/
}