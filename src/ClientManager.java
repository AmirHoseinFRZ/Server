import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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

            if(in.readUTF().equals("signUp"))
            {
                signUp();
                System.out.println("signUp");
                if(in.readUTF().equals("createAccount"))
                {
                    createAccount();
                    System.out.println("createAccount");
                    if(in.readUTF().equals("enter"))
                    {
                        enter();
                        System.out.println("enter");
                        String clientRequest = in.readUTF();
                        if(clientRequest.equals("transmission"))
                        {
                            transmission();
                        }
                        else if(clientRequest.equals("payment"))
                        {
                            //payment();
                        }
                        else if(clientRequest.equals("logOut"))
                        {
                            //logOut
                        }
                        else if(clientRequest.equals("logOut"))
                        {
                            //loan();
                        }
                        else if(clientRequest.equals("information"))
                        {
                            seeingInformationOfAccounts();
                        }
                        else if(clientRequest.equals("createAccount"))
                        {
                            createAccount();
                            if(in.readUTF().equals("enter"))//badan dorost mishe
                                enter();

                        }
                        else
                            System.out.println("back");
                    }
                    else
                        System.out.println("back");
                }
                else
                {
                    System.out.println("back");
                }
            }
            else
            {
                signIn();
                if(in.readUTF().equals("LogIn"))
                {
                    logIn();
                    if (in.readUTF().equals("enter"))
                    {
                        enter();
                        System.out.println("enter");
                        String clientRequest = in.readUTF();
                        System.out.println(clientRequest);
                        if(clientRequest.equals("transmission"))
                        {
                            transmission();
                        }
                        else if(clientRequest.equals("payment"))
                        {
                            //payment();
                        }
                        else if(clientRequest.equals("logOut"))
                        {
                            //logOut
                        }
                        else if(clientRequest.equals("logOut"))
                        {
                            //loan();
                        }
                        else if(clientRequest.equals("information"))
                        {
                            seeingInformationOfAccounts();
                        }
                        else if(clientRequest.equals("createAccount"))
                        {
                            createAccount();
                            if(in.readUTF().equals("enter"))
                                enter();
                        }
                        //else
                           //System.out.println("back");
                    }
                    //else
                        //System.out.println("back");
                }
                //else {
                  //  System.out.println("back");
                }
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void signUp()
    {
        try {
            if(!(in.readBoolean()))
                signUp();
            else
            {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void signIn()
    {
        try {
            System.out.println("signIn");
            String nationalCode = in.readUTF();
                String passWord = in.readUTF();
                nationalCode_PassWord = nationalCode + "-" + passWord;
                File file = new File("src/" + nationalCode + "-" + passWord);
                if(file.exists())
                    out.writeBoolean(true);
                else
                {
                    out.writeBoolean(false);
                    signIn();
                }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logIn()
    {
        try {
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
            }
            else
            {
                out.writeBoolean(false);
                logIn();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void seeingInformationOfAccounts()
    {
        try {
            File userInformation = new File("src/" + nationalCode_PassWord + "/information.txt");
            Scanner scanner = new Scanner(userInformation);
            int i = 0;
            while (i < 4)
            {
                out.writeUTF(scanner.nextLine());
                i++;
            }
            File accountInformation = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number +".txt");
            Scanner scanner1 = new Scanner(accountInformation);
            i = 0;
            while (i < 4)
            {
                out.writeUTF(scanner1.nextLine());
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enter()
    {
        try {
            File accountInformation = new File("src/" + nationalCode_PassWord + "/" + alias_Password + "." + number + ".txt");
            Scanner scanner = new Scanner(accountInformation);
            int i = 0;
            while (i < 2) {
                out.writeUTF(scanner.nextLine());
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void  transmission() throws IOException {
        System.out.println("search");
        if(in.readUTF().equals("search")) {
            try {
                int sw = 0;
                String destinationNumber = in.readUTF();
                String amount = in.readUTF();
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
                if (destinationFile != null) {
                    out.writeBoolean(true);
                    Scanner scanner1 = new Scanner(destinationFile);
                    String destinationAlias = scanner1.nextLine();
                    out.writeUTF(destinationAlias);
                    String username = null;
                    ArrayList<String> folderNameC = new ArrayList<String>();
                    for (int x = 0; x < folderName.length(); x++)
                        folderNameC.add(String.valueOf(folderName.charAt(x)));
                    int x = 0;
                    while (!(folderNameC.get(x).equals("-"))) {
                        if (x == 0)
                            username = folderNameC.get(x);
                        else
                            username += folderNameC.get(x);
                        x++;
                    }
                    out.writeUTF(username);
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
                    } else {
                        out.writeBoolean(false);
                        transmission();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}