public class Account {
    public String accountNumber;
    public double accountBalance;
    public int accountType;//0 -> current account, 1 -> short saving account, 2 -> long saving account, 3 -> flat account
    public String passWord;
    public String alias;

    public Account(String passWord, String alias, int accountType){
        this.passWord = passWord;
        this.alias = alias;
        accountBalance = 1000.0;
        this.accountType = accountType;
        int accountNumber = ((int) (Math.random() * 100000000));
        this.accountNumber = String.valueOf(accountNumber);
    }

}