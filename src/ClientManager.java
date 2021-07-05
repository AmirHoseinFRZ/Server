import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

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
            if(in.readUTF().equals("continue"))
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
                    enter();
                }
                else
                {
                    out.writeBoolean(false);
                    logIn();
                    System.out.println("false");
                }
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
                        fileWriter.append("loan    "+ "+ " + Double.valueOf(amount) + '\n');
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
                                fileWriter.append("payment    " + "- " + amount + '\n');
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
                        account.delete();
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
                        if(destinationFile != null)
                        {
                            out.writeBoolean(true);
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
                            fileReader.close();
                            bufferedReader.close();
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
                            file2.delete();
                            File file1 = new File("src/" + nationalCode_PassWord + "/loan." + alias_Password + ".txt");
                            if (file1.exists())
                                file1.delete();
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
}