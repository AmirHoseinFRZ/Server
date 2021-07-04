public class Account {
    public String accountNumber;
    public double accountBalance;
    public String accountType;//0 -> current account, 1 -> short saving account, 2 -> long saving account, 3 -> flat account
    public String passWord;
    public String alias;

    public Account(String passWord, String alias, String accountType){
        this.passWord = passWord;
        this.alias = alias;
        accountBalance = 1000.0;
        this.accountType = accountType;
        int accountNumber = ((int) (Math.random() * 100000000));
        if(accountNumber >= 10000000)
            this.accountNumber = String.valueOf(accountNumber);
        else
            this.accountNumber = "0" + String.valueOf(accountNumber);
    }

}