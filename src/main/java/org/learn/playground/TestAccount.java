package org.learn.playground;

public class TestAccount {
    private static Account account;

    public static void main(String[] args) {
        account = new Account();
        System.out.println("0");
        account.withdraw(5);
        System.out.println("1");
        account.withdraw(100);
        System.out.println("2");
    }
}
