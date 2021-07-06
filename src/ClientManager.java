import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ClientManager implements Runnable {
    Socket client;
    String nationalCode_PassWord;
    String alias_Password;
    String number;
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
            check();
            loanCheck();
            signUpOrSignIn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void signUpOrSignIn()
    {
        try {
            System.out.println("signUpOrSignIn");
            if(in.readUTF().equals("signUp"))
                signUp();
            else
                signIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void signUp()
    {
        try {
            if(in.readUTF().equals("continue"))
            {
                if(!(in.readBoolean()))
                    signUp();
                else
                {
                    System.out.println("signUp");
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
                    if(in.readUTF().equals("createAccount"))
                        createAccount();
                }
            }
            else
            {
                signUpOrSignIn();
                System.out.println("gotofirst");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void signIn()
    {
        try {
            if (in.readUTF().equals("continue"))
            {
                System.out.println("signIn");
                String nationalCode = in.readUTF();
                String passWord = in.readUTF();
                nationalCode_PassWord = nationalCode + "-" + passWord;
                File file = new File("src/" + nationalCode + "-" + passWord);
                if(file.exists())
                {
                    out.writeBoolean(true);
                    logIn();
                }
                else
                {
                    out.writeBoolean(false);
                    signIn();
                }
            }
            else
                signUpOrSignIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createAccount()
    {
        try {
            System.out.println("createAccount");
            String accountType = in.readUTF();//0 -> current account, 1 -> short saving account, 2 -> long saving account, 3 -> flat account
            String passWord = in.readUTF();
            String alias = in.readUTF();
            alias_Password = alias + "-" + passWord;
            Account account = new Account(passWord, alias, accountType);
            number = account.accountNumber;
            File file = new File("src/" + nationalCode_PassWord + "/" + account.alias + "-" + account.passWord + "." + number + ".txt");
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(account.alias);
            printWriter.println(account.accountNumber);
            printWriter.println(account.accountType);
            printWriter.println(account.accountBalance);
            printWriter.println(account.passWord);
            printWriter.close();
            File file1 = new File("src/" + nationalCode_PassWord + "/transaction" + account.alias + "-" + account.passWord + ".txt");
            PrintWriter printWriter1 = new PrintWriter(file1);
            printWriter1.print("");
            if(in.readUTF().equals("enter"))
                enter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logIn()
    {
        try {
            String clientRequest = in.readUTF();
            if(clientRequest.equals("continue"))
            {
                System.out.println("logIn");
                String alias = in.readUTF();
                String passWord = in.readUTF();
                alias_Password = alias + "-" + passWord;
                File file = new File("src/" + nationalCode_PassWord);
                ArrayList<String> names = new ArrayList<String>();
                for (int z = 0; z < file.list().length; z++)
                    names.add(file.list()[z]);
                String name = "0";
                int i = 0;
                while(i < names.size())
                {
                    if(names.get(i).contains(alias + "-" + passWord + ".") && !(names.get(i).contains("transaction")))
                    {
                        name = names.get(i);
                        break;
                    }
                    i++;
                }
                if(name != "0")
                {
                    for (int j = name.length() - 12; j <= name.length() - 5; j++)
                    {
                        if(j == name.length() - 12)
                            number = name.charAt(j) + "";
                        else
                            number += name.charAt(j);
                    }
                    out.writeBoolean(true);
                    /*File file1 = new File("src/" + nationalCode_PassWord + "/information.txt");
                    FileReader fileReader = new FileReader(file1);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String to = "";
                    for(int x = 0; x < 5; x++)
                    {
                        String line = bufferedReader.readLine();
                        if(x == 3)
                            to = line;
                    }
                    String from = "dehnavi_firoozi_bank@yahoo.com";
                    String host = "localhost";
                    Properties properties = System.getProperties();
                    properties.setProperty("mail.smtp.host", host);
                    Session session = Session.getDefaultInstance(properties);
                    try {
                        MimeMessage message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(from));
                        message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                        message.setSubject("Ping");
                        message.setText("You have logged in your account!");
                        Transport.send(message);
                        System.out.println("message sent successfully....");
                    }
                    catch (MessagingException messagingException) {
                        messagingException.printStackTrace();
                    }*/
                    enter();
                }
                else
                {
                    out.writeBoolean(false);
                    logIn();
                    System.out.println("false");
                }
            }
            else if(clientRequest.equals("most"))
            {
                System.out.println("most used selected");
                File folder = new File("src/" + nationalCode_PassWord);
                ArrayList<String> transactionNames = new ArrayList<String>();
                for (int z = 0; z < folder.list().length; z++)
                {
                    if(folder.list()[z].contains("transaction"))
                        transactionNames.add(folder.list()[z]);
                }
                ArrayList<File> transactionFiles = new ArrayList<File>();
                int i = 0;
                while (i < transactionNames.size())
                {
                    File file = new File("src/" + nationalCode_PassWord + "/" + transactionNames.get(i));
                    transactionFiles.add(file);
                    i++;
                }
                i = 0;
                File mostUsed = null;
                long length = transactionFiles.get(0).length();
                while (i < transactionNames.size())
                {
                    if(transactionFiles.get(i).length() >= length)
                    {
                        mostUsed = transactionFiles.get(i);
                        length = transactionFiles.get(i).length();
                    }
                    i++;
                }
                String mostUsedAlias_Password = "";
                for(int j = 11; j < mostUsed.getName().length() - 4; j++)
                    mostUsedAlias_Password += mostUsed.getName().charAt(j);
                alias_Password = mostUsedAlias_Password;
                String name = "";
                System.out.println(alias_Password);
                for (int j = 0; j < folder.list().length; j++)
                {
                    if(folder.list()[j].contains(alias_Password) && !(folder.list()[j].contains("transaction")))
                    {
                        name = folder.list()[j];
                        break;
                    }
                }
                for (int j = name.length() - 12; j <= name.length() - 5; j++)
                {
                    if(j == name.length() - 12)
                        number = name.charAt(j) + "";
                    else
                        number += name.charAt(j);
                }
                System.out.println(number);
                System.out.println(100);
                enter();
            }
            else
                signUpOrSignIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void seeingInformationOfAccounts()
    {
        try {
            System.out.println("personalInfo");
            File userInformation = new File("src/" + nationalCode_PassWord + "/information.txt");
            FileReader fileReader = new FileReader(userInformation);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int i = 0;
            while (i < 4)
            {
                out.writeUTF(bufferedReader.readLine());
                i++;
            }
            fileReader.close();
            bufferedReader.close();
            File accountInformation = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number +".txt");
            FileReader fileReader1 = new FileReader(accountInformation);
            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
            i = 0;
            while (i < 4)
            {
                out.writeUTF(bufferedReader1.readLine());
                i++;
            }
            fileReader1.close();
            bufferedReader1.close();
            File transaction = new File("src/" + nationalCode_PassWord + "/transaction" + alias_Password + ".txt");
            FileReader fileReader2 = new FileReader(transaction);
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
            String information = "";
            String line = "";
            while ((line = bufferedReader2.readLine()) != null)
            {
                information += line + '\n';
            }
            fileReader2.close();
            bufferedReader2.close();
            out.writeUTF(information);
            if(in.readUTF().equals("back"))
                enter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enter()
    {
        try {
            System.out.println("enter");
            File accountInformation = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number + ".txt");
            FileReader fileReader = new FileReader(accountInformation);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int i = 0;
            while (i < 2) {
                out.writeUTF(bufferedReader.readLine());
                i++;
            }
            fileReader.close();
            bufferedReader.close();
            String clientRequest = in.readUTF();
            if(clientRequest.equals("transmission"))
                transmission();
            else if(clientRequest.equals("payment"))
                payment();
            else if(clientRequest.equals("logOut"))
                logOut();
            else if(clientRequest.equals("loan"))
                loan();
            else if(clientRequest.equals("information"))
                seeingInformationOfAccounts();
            else if(clientRequest.equals("createAccount"))
                createAccount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void  transmission() throws IOException {
        System.out.println("search");
        String clientRequest = in.readUTF();
        if(clientRequest.equals("search")) {
            try {
                int sw = 0;
                String destinationNumber = in.readUTF();
                String amount = in.readUTF();
                if(in.readBoolean())
                {
                    String folderName = "";
                    String destinationAlis_Password_number = "";
                    File destinationFile = null;

                    File folder = new File("src");
                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < folder.list().length; i++)
                        names.add(folder.list()[i]);
                    int i = 0;
                    while (i < names.size() && sw == 0) {
                        File file = new File("src/" + names.get(i));
                        if (file.isDirectory()) {
                            ArrayList<String> filenames = new ArrayList<String>();
                            for (int j = 0; j < file.list().length; j++)
                                filenames.add(file.list()[j]);
                            int z = 0;
                            while (z < filenames.size()) {
                                if (filenames.get(z).contains(destinationNumber) && !(filenames.get(z).contains("transaction"))) {
                                    System.out.println("if");
                                    File file1 = new File("src/" + file.getName() + "/" + filenames.get(z));
                                    destinationFile = file1;
                                    destinationAlis_Password_number = filenames.get(z);
                                    folderName = file.getName();
                                    sw = 1;
                                    break;
                                }
                                z++;
                            }
                        }
                        i++;
                    }
                    if (destinationFile != null)
                    {
                        System.out.println("if2");
                        out.writeBoolean(true);
                        Scanner scanner1 = new Scanner(destinationFile);
                        String destinationAlias = scanner1.nextLine();
                        //scanner1.close();
                        out.writeUTF(destinationAlias);
                        File file6 = new File("src/" + folderName + "/information.txt");
                        FileReader fileReader6 = new FileReader(file6);
                        BufferedReader bufferedReader6 = new BufferedReader(fileReader6);
                        String username = bufferedReader6.readLine();
                        out.writeUTF(username);
                        System.out.println(25);
                        System.out.println(username);
                        System.out.println("beforetransmit");
                        if (in.readUTF().equals("transmit")) {
                            System.out.println("transmit");
                            String information = "0";
                            i = 0;
                            FileReader fileReader = new FileReader(destinationFile);
                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            while (i < 5) {
                                if (i == 3) {
                                    information += String.valueOf((Double.valueOf(bufferedReader.readLine()) + Double.valueOf(amount))) + '\n';
                                } else {
                                    if (i == 0)
                                        information = bufferedReader.readLine() + '\n';
                                    else
                                        information += bufferedReader.readLine() + '\n';
                                }
                                i++;
                            }
                            fileReader.close();
                            bufferedReader.close();
                            String information1 = "";
                            File file3 = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number + ".txt");
                            FileReader fileReader1 = new FileReader(file3);
                            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                            i = 0;
                            Double accountsBalance = 0.0;
                            while (i < 5) {
                                if (i == 3) {
                                    accountsBalance = Double.valueOf(bufferedReader1.readLine());
                                    information1 += String.valueOf((accountsBalance - Double.valueOf(amount))) + '\n';
                                }
                                else
                                {
                                    if (i == 0)
                                        information1 = bufferedReader1.readLine() + '\n';
                                    else
                                        information1 += bufferedReader1.readLine() + '\n';
                                }
                                i++;
                            }
                            fileReader1.close();
                            bufferedReader1.close();
                            if(accountsBalance < Double.valueOf(amount))
                            {
                                out.writeBoolean(false);
                                transmission();
                            }
                            else
                            {
                                out.writeBoolean(true);
                                PrintWriter printWriter = new PrintWriter(destinationFile);
                                printWriter.print(information);
                                printWriter.close();
                                PrintWriter printWriter1 = new PrintWriter(file3);
                                printWriter1.print(information1);
                                printWriter1.close();
                                String destinationAlias_PassWord = "";
                                for (int j = 0; j < destinationAlis_Password_number.length() - 13; j++)
                                {
                                    if(j == 0)
                                        destinationAlias_PassWord = destinationAlis_Password_number.charAt(j) + "";
                                    else
                                        destinationAlias_PassWord += destinationAlis_Password_number.charAt(j);
                                }
                                System.out.println(destinationAlias_PassWord);
                                File file4 = new File("src/" + folderName + "/" + "transaction" + destinationAlias_PassWord + ".txt");
                                FileWriter fileWriter = new FileWriter(file4, true);
                                fileWriter.append("Transmission    " + number + "    " + "+ " + Double.valueOf(amount) + '\n');
                                fileWriter.close();
                                File file5 = new File("src/" + nationalCode_PassWord + "/" + "transaction" + alias_Password+ ".txt");
                                FileWriter fileWriter1 = new FileWriter(file5, true);
                                fileWriter1.append("Transmission    " + destinationNumber + "    " + "- " + Double.valueOf(amount) + '\n');
                                fileWriter1.close();
                                enter();
                            }
                        }
                        else
                        {
                            System.out.println("elseTransmit");
                            enter();
                        }
                    }
                    else
                    {
                        System.out.println("else2");
                        out.writeBoolean(false);
                        transmission();
                    }
                }
                else
                    transmission();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else//back
        {
            System.out.println("backToEnter");
            enter();
        }
    }
    public void loan()
    {
        try {
            if(in.readUTF().equals("done"))
            {
                if(in.readBoolean())
                {
                    String amount = in.readUTF();
                    String payback = in.readUTF();
                    File file = new File("src/" + nationalCode_PassWord + "/" + "loan." + alias_Password + ".txt");
                    if(file.exists())
                    {
                        out.writeBoolean(false);
                        loan();
                    }
                    else
                    {
                        out.writeBoolean(true);
                        PrintWriter printWriter = new PrintWriter(file);
                        printWriter.println(amount);
                        printWriter.println(payback);
                        printWriter.println("0");
                        printWriter.close();
                        File file1 = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number + ".txt");
                        FileReader fileReader = new FileReader(file1);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String information = new String();
                        int i = 0;
                        while (i < 5) {
                            if (i == 3) {
                                information += String.valueOf((Double.valueOf(bufferedReader.readLine()) + Double.valueOf(amount))) + '\n';
                            } else {
                                if (i == 0)
                                    information = bufferedReader.readLine() + '\n';
                                else
                                    information += bufferedReader.readLine() + '\n';
                            }
                            System.out.println(information);
                            i++;
                        }
                        PrintWriter printWriter1 = new PrintWriter(file1);
                        printWriter1.print(information);
                        fileReader.close();
                        bufferedReader.close();
                        printWriter1.close();
                        File file2 = new File("src/" + nationalCode_PassWord + "/" + "transaction" + alias_Password+ ".txt");
                        FileWriter fileWriter = new FileWriter(file2, true);
                        fileWriter.append("loan    " + "+ " + Double.valueOf(amount) + '\n');
                        fileWriter.close();
                        enter();
                    }
                }
                else
                    loan();
            }
            else
                enter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void payment()
    {
        try {
            if(in.readUTF().equals("search"))
            {
                System.out.println("search");
                if(in.readBoolean())
                {
                    System.out.println(0);
                    String billsNumber = in.readUTF();
                    File bill = new File("src/payment/" + billsNumber + ".txt");
                    if(bill.exists())
                    {
                        System.out.println(1);
                        out.writeBoolean(true);
                        FileReader fileReader = new FileReader(bill);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String amount = bufferedReader.readLine();
                        System.out.println(amount);
                        String billsName = bufferedReader.readLine();
                        System.out.println(billsName);
                        fileReader.close();
                        bufferedReader.close();
                        out.writeUTF(amount);
                        out.writeUTF(billsName);
                        System.out.println(2);
                        if(in.readUTF().equals("pay"))
                        {
                            System.out.println("pay");
                            File file = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number + ".txt");
                            FileReader fileReader1 = new FileReader(file);
                            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                            int i = 0;
                            String information = "";
                            Double accountBalance = 0.0;
                            while (i < 5)
                            {
                                if(i == 3)
                                {
                                    accountBalance = Double.valueOf(bufferedReader1.readLine());
                                    information += String.valueOf(accountBalance - Double.valueOf(amount)) + '\n';
                                }
                                else
                                {
                                    if (i == 0)
                                        information = bufferedReader1.readLine() + '\n';
                                    else
                                        information += bufferedReader1.readLine() + '\n';
                                }
                                i++;
                            }
                            fileReader1.close();
                            bufferedReader1.close();
                            if(accountBalance > Double.valueOf(amount))
                            {
                                out.writeBoolean(true);
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                                bill.delete();
                                File transaction = new File("src/" + nationalCode_PassWord + "/" + "transaction" + alias_Password+ ".txt");
                                FileWriter fileWriter = new FileWriter(transaction, true);
                                fileWriter.append("payment    " + billsNumber + "    - " + amount + '\n');
                                fileWriter.close();
                                enter();
                            }
                            else
                            {
                                System.out.println(1);
                                out.writeBoolean(false);
                                enter();
                            }
                        }
                        else//back
                            enter();
                    }
                    else
                    {
                        out.writeBoolean(false);
                        payment();
                    }
                }
                else
                    payment();
            }
            else
                enter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logOut()
    {
        try {
            String clientRequest = in.readUTF();
            if(clientRequest.equals("logOut"))
                signUpOrSignIn();
            else if (clientRequest.equals("delete"))
            {
                System.out.println("delete");
                if(in.readBoolean())
                {
                    System.out.println("after delete");
                    String passWord = in.readUTF();
                    String destinationNumber = in.readUTF();
                    if(alias_Password.contains(passWord))
                    {
                        System.out.println("correct password");
                        out.writeBoolean(true);
                        File account = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number + ".txt");
                        FileReader fileReader = new FileReader(account);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        int i = 0;
                        String amount = "";
                        while (i < 4)
                        {
                            if(i == 3)
                                amount = bufferedReader.readLine();
                            else
                                amount = bufferedReader.readLine();
                            i++;
                        }
                        fileReader.close();
                        bufferedReader.close();
                        System.out.println(amount);
                        String folderName = "";
                        String destinationAlis_Password_number = "";
                        File destinationFile = null;
                        int sw = 0;
                        File folder = new File("src");
                        ArrayList<String> names = new ArrayList<String>();
                        for (int j = 0; j < folder.list().length; j++)
                            names.add(folder.list()[j]);
                        i = 0;
                        while (i < names.size() && sw == 0) {
                            File file = new File("src/" + names.get(i));
                            if (file.isDirectory()) {
                                ArrayList<String> filenames = new ArrayList<String>();
                                for (int j = 0; j < file.list().length; j++)
                                    filenames.add(file.list()[j]);
                                int z = 0;
                                while (z < filenames.size()) {
                                    if (filenames.get(z).contains("." + destinationNumber) && !(filenames.get(z).contains("transaction"))) {
                                        System.out.println("if");
                                        File file1 = new File("src/" + file.getName() + "/" + filenames.get(z));
                                        destinationFile = file1;
                                        destinationAlis_Password_number = filenames.get(z);
                                        folderName = file.getName();
                                        sw = 1;
                                        break;
                                    }
                                    z++;
                                }
                            }
                            i++;
                        }
                        if(destinationFile != null)
                        {
                            out.writeBoolean(true);
                            Files.delete(account.toPath());
                            String information = "0";
                            i = 0;
                            FileReader fileReader1 = new FileReader(destinationFile);
                            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                            while (i < 5) {
                                if (i == 3) {
                                    information += String.valueOf((Double.valueOf(bufferedReader1.readLine()) + Double.valueOf(amount))) + '\n';
                                } else {
                                    if (i == 0)
                                        information = bufferedReader1.readLine() + '\n';
                                    else
                                        information += bufferedReader1.readLine() + '\n';
                                }
                                i++;
                            }
                            fileReader1.close();
                            bufferedReader1.close();
                            PrintWriter printWriter = new PrintWriter(destinationFile);
                            printWriter.print(information);
                            printWriter.close();
                            String destinationAlias_PassWord = "";
                            for (int j = 0; j < destinationAlis_Password_number.length() - 13; j++)
                            {
                                if(j == 0)
                                    destinationAlias_PassWord = destinationAlis_Password_number.charAt(j) + "";
                                else
                                    destinationAlias_PassWord += destinationAlis_Password_number.charAt(j);
                            }
                            File file4 = new File("src/" + folderName + "/" + "transaction" + destinationAlias_PassWord + ".txt");
                            FileWriter fileWriter = new FileWriter(file4, true);
                            fileWriter.append("Transmission    " + number + "    " + "+ " + Double.valueOf(amount) + '\n');
                            fileWriter.close();
                            File file2 = new File("src/" + nationalCode_PassWord + "/transaction" + alias_Password + ".txt");
                            Files.delete(file2.toPath());
                            File file1 = new File("src/" + nationalCode_PassWord + "/loan." + alias_Password + ".txt");
                            if (file1.exists())
                                Files.delete(file1.toPath());
                            signUpOrSignIn();
                        }
                        else
                        {
                            out.writeBoolean(false);
                            logOut();
                        }
                    }
                    else
                    {
                        out.writeBoolean(false);
                        logOut();
                    }
                }
                else
                    logOut();
            }
            else//back
                enter();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void loanCheck() throws IOException {
        File src = new File("src/");
        int i = 0;
        while (i < src.list().length)
        {
            File user = new File("src/" + src.list()[i]);
            if(!(user.getName().equals("payment")) && user.isDirectory())
            {
                int j = 0;
                while (j < user.list().length)
                {
                    File file = new File("src/" + user.getName() + "/" + user.list()[j]);
                    if(!(file.getName().contains("loan")) && file.getName().contains("."))
                    {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        long creationTime = attributes.creationTime().to(TimeUnit.MILLISECONDS);
                        Date date = new Date();
                        System.out.println(date.getTime());
                        System.out.println(creationTime);
                        long time = Long.valueOf(date.getTime());
                        if((time - creationTime) % 2592000 == 1)
                        {
                            FileReader fileReader = new FileReader(file);
                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            String amount = "";
                            String alias = "";
                            String password = "";
                            int z = 0;
                            for(int x = 0; x < 5; x++)
                            {
                                String line = bufferedReader.readLine();
                                if (x == 1)
                                    alias = line;
                                if(x == 3)
                                    amount = line;
                                if (x == 4)
                                    password = line;
                            }
                            fileReader.close();
                            bufferedReader.close();
                            /////////////////////////////////////////////////////////////////////////////////////////////new loan file
                            File file1 = new File("src/" + user.getName() + "/loan." + alias + "-" + password + ".txt");
                            FileReader fileReader1 = new FileReader(file1);
                            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                            String loanAmount = bufferedReader1.readLine();
                            String loanType = bufferedReader1.readLine();
                            String payed = bufferedReader1.readLine();
                            fileReader1.close();
                            bufferedReader1.close();
                            System.out.println("amount is :" + loanAmount);
                            System.out.println("loan type is :" + loanType);
                            System.out.println(payed);
                            String information = "";
                            if(loanType.equals("one month"))
                            {
                                amount = String.valueOf(Double.valueOf(amount) - (Double.valueOf(loanAmount)));
                                i = 0;
                                FileReader fileReader2 = new FileReader(file);
                                BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
                                while (i < 5) {
                                    String line = bufferedReader2.readLine();
                                    if (i == 3) {
                                        information += String.valueOf(Double.valueOf(amount)) + '\n';
                                    } else {
                                        if (i == 0)
                                            information = line + '\n';
                                        else
                                            information += line + '\n';
                                    }
                                    i++;
                                }
                                fileReader1.close();
                                bufferedReader1.close();
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                                File file2 = new File("src/" + user.getName() + "/" + "transaction" + alias + "-" + password + ".txt");
                                FileWriter fileWriter = new FileWriter(file2, true);
                                fileWriter.append("loan    " + "- " + Double.valueOf(loanAmount) + '\n');
                                fileWriter.close();
                                file1.delete();
                            }
                            else if(loanType.equals("three month"))
                            {
                                amount = String.valueOf(Double.valueOf(amount) - (Double.valueOf(loanAmount)) / 3);
                                i = 0;
                                FileReader fileReader2 = new FileReader(file);
                                BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
                                while (i < 5) {
                                    String line = bufferedReader2.readLine();
                                    if (i == 3) {
                                        information += String.valueOf(Double.valueOf(amount)) + '\n';
                                    } else {
                                        if (i == 0)
                                            information = line + '\n';
                                        else
                                            information += line + '\n';
                                    }
                                    i++;
                                }
                                fileReader1.close();
                                bufferedReader1.close();
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                                information = "";
                                FileReader fileReader3 = new FileReader(file1);
                                BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
                                information = bufferedReader3.readLine() + '\n';
                                information += bufferedReader3.readLine() + '\n';
                                information += String.valueOf(Integer.parseInt(payed) + 1) + '\n';
                                PrintWriter printWriter1 = new PrintWriter(file1);
                                printWriter1.print(information);
                                printWriter1.close();
                                File file2 = new File("src/" + user.getName() + "/" + "transaction" + alias + "-" + password + ".txt");
                                FileWriter fileWriter = new FileWriter(file2, true);
                                fileWriter.append("loan    " + "- " + Double.valueOf(loanAmount) / 3 + '\n');
                                fileWriter.close();
                                if (Integer.parseInt(payed) == 2)
                                    file1.delete();
                            }
                            else if(loanType.equals("six month"))
                            {
                                amount = String.valueOf(Double.valueOf(amount) - (Double.valueOf(loanAmount)) / 6);
                                i = 0;
                                FileReader fileReader2 = new FileReader(file);
                                BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
                                while (i < 5) {
                                    String line = bufferedReader2.readLine();
                                    if (i == 3) {
                                        information += String.valueOf(Double.valueOf(amount)) + '\n';
                                    } else {
                                        if (i == 0)
                                            information = line + '\n';
                                        else
                                            information += line + '\n';
                                    }
                                    i++;
                                }
                                fileReader1.close();
                                bufferedReader1.close();
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                                information = "";
                                FileReader fileReader3 = new FileReader(file1);
                                BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
                                information = bufferedReader3.readLine() + '\n';
                                information += bufferedReader3.readLine() + '\n';
                                information += String.valueOf(Integer.parseInt(payed) + 1) + '\n';
                                PrintWriter printWriter1 = new PrintWriter(file1);
                                printWriter1.print(information);
                                printWriter1.close();
                                File file2 = new File("src/" + user.getName() + "/" + "transaction" + alias + "-" + password + ".txt");
                                FileWriter fileWriter = new FileWriter(file2, true);
                                fileWriter.append("loan    " + "- " + Double.valueOf(loanAmount) / 6 + '\n');
                                fileWriter.close();
                                if (Integer.parseInt(payed) == 5)
                                    file1.delete();
                            }
                            else if(loanType.equals("one year"))
                            {
                                amount = String.valueOf(Double.valueOf(amount) - (Double.valueOf(loanAmount)) / 12);
                                i = 0;
                                FileReader fileReader2 = new FileReader(file);
                                BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
                                while (i < 5) {
                                    String line = bufferedReader2.readLine();
                                    if (i == 3) {
                                        information += String.valueOf(Double.valueOf(amount)) + '\n';
                                    } else {
                                        if (i == 0)
                                            information = line + '\n';
                                        else
                                            information += line + '\n';
                                    }
                                    i++;
                                }
                                fileReader1.close();
                                bufferedReader1.close();
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                                information = "";
                                FileReader fileReader3 = new FileReader(file1);
                                BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
                                information = bufferedReader3.readLine() + '\n';
                                information += bufferedReader3.readLine() + '\n';
                                information += String.valueOf(Integer.parseInt(payed) + 1) + '\n';
                                PrintWriter printWriter1 = new PrintWriter(file1);
                                printWriter1.print(information);
                                printWriter1.close();
                                File file2 = new File("src/" + user.getName() + "/" + "transaction" + alias + "-" + password + ".txt");
                                FileWriter fileWriter = new FileWriter(file2, true);
                                fileWriter.append("loan    " + "- " + Double.valueOf(loanAmount) / 12 + '\n');
                                fileWriter.close();
                                if (Integer.parseInt(payed) == 11)
                                    file1.delete();
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }
    }
    public void check() throws IOException {
        File src = new File("src/");
        int i = 0;
        while (i < src.list().length)
        {
            File user = new File("src/" + src.list()[i]);
            if(!(user.getName().equals("payment")) && user.isDirectory())
            {
                int j = 0;
                while (j < user.list().length)
                {
                    File file = new File("src/" + user.getName() + "/" + user.list()[j]);
                    if(!(file.getName().contains("loan") || file.getName().contains("transaction") || file.getName().contains("information")))
                    {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        long creationTime = attributes.creationTime().to(TimeUnit.MILLISECONDS);
                        Date date = new Date();
                        System.out.println(date.getTime());
                        System.out.println(creationTime);
                        long time = Long.valueOf(date.getTime());
                        if((time - creationTime) % 2592000 == 1)
                        {
                            FileReader fileReader = new FileReader(file);
                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            String information = "";
                            String accountType = "";
                            String amount = "";
                            for(int x = 0; x < 5; x++)
                            {
                                String line = bufferedReader.readLine();
                                if(x == 2)
                                {
                                    information += line;
                                    accountType = line;
                                }
                                if(x == 3)
                                {
                                    information += line;
                                    amount = line;
                                }
                                else
                                    information += line;
                            }
                            fileReader.close();
                            bufferedReader.close();
                            System.out.println("amount is :" + amount);
                            System.out.println("account type is :" + accountType);
                            if(accountType.equals("short saving"))
                            {
                                amount = String.valueOf(Double.valueOf(amount) + Double.valueOf(amount) * 0.07);
                                int z = 0;
                                FileReader fileReader1 = new FileReader(file);
                                BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                                while (z < 5) {
                                    if (z == 3) {
                                        information += String.valueOf((Double.valueOf(bufferedReader1.readLine()) + Double.valueOf(amount))) + '\n';
                                    } else {
                                        if (z == 0)
                                            information = bufferedReader1.readLine() + '\n';
                                        else
                                            information += bufferedReader1.readLine() + '\n';
                                    }
                                    z++;
                                }
                                fileReader1.close();
                                bufferedReader1.close();
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                            }
                            else if(accountType.equals("long saving") && ((Long.valueOf(time) - creationTime) / 2592000) % 12== 0)
                            {
                                amount = String.valueOf(Double.valueOf(amount) + Double.valueOf(amount) * 0.15);
                                int z = 0;
                                FileReader fileReader1 = new FileReader(file);
                                BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                                while (z < 5) {
                                    if (z == 3) {
                                        information += String.valueOf((Double.valueOf(bufferedReader1.readLine()) + Double.valueOf(amount))) + '\n';
                                    } else {
                                        if (z == 0)
                                            information = bufferedReader1.readLine() + '\n';
                                        else
                                            information += bufferedReader1.readLine() + '\n';
                                    }
                                    z++;
                                }
                                fileReader1.close();
                                bufferedReader1.close();
                                PrintWriter printWriter = new PrintWriter(file);
                                printWriter.print(information);
                                printWriter.close();
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }
    }
}