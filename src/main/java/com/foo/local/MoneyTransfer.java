package com.foo.local;

import com.foo.data.Account;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.TransactionController;


public class MoneyTransfer {

    private CacheManager cacheManager = new CacheManager();
    private Ehcache accountsCache = cacheManager.getCache("accountsCache");

    private void init() {
        if (accountsCache.get("000123") == null) {
            Account account = new Account("000123", 500.0);
            System.out.println("loading account in cache: " + account);
            accountsCache.put(new Element("000123", account));
        }
        if (accountsCache.get("000456") == null) {
            Account account = new Account("000456", 500.0);
            System.out.println("loading account in cache: " + account);
            accountsCache.put(new Element("000456", account));
        }
    }

    private void transfer(String fromAccountId, String toAccountId, double amount) {
        Account fromAccount = (Account) accountsCache.get(fromAccountId).getObjectValue();
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        accountsCache.put(new Element(fromAccount.getId(), fromAccount));

        generateError();

        Account toAccount = (Account) accountsCache.get(toAccountId).getObjectValue();
        toAccount.setBalance(toAccount.getBalance() + amount);
        accountsCache.put(new Element(toAccount.getId(), toAccount));
    }

    private void showBalances(String... accountIds) {
        for (String accountId : accountIds) {
            Element element = accountsCache.get(accountId);
            if (element != null) {
                Account account = (Account) element.getObjectValue();
                System.out.println("account #" + accountId + " with balance " + account.getBalance());
            } else {
                System.out.println("account #" + accountId + " is null");
            }
        }
    }

    private void generateError() {
        throw new RuntimeException("error in the middle of the transfer!");
    }

    private void shutdown() {
        cacheManager.shutdown();
    }


    public static void main(String[] args) throws Exception {
        MoneyTransfer moneyTransfer = new MoneyTransfer();

        TransactionController transactionController = moneyTransfer.cacheManager.getTransactionController();

        transactionController.begin();

        moneyTransfer.init();

        transactionController.commit();

        try {
            transactionController.begin();

            System.out.println("\n*** before transfer ***");
            moneyTransfer.showBalances("000123", "000456");

            /*
              transfer $100 from 000123 to 000456
             */
            System.out.println("\nExecuting money transfer");
            moneyTransfer.transfer("000123", "000456", 100);

            transactionController.commit();
        } catch (Exception ex) {
            transactionController.rollback();
            System.out.println("*** caught exception: " + ex);
        } finally {
            /*
              printing balances after outcome
             */
            System.out.println("\n*** after transfer ***");

            transactionController.begin();
            moneyTransfer.showBalances("000123", "000456");
            transactionController.commit();

            moneyTransfer.shutdown();
        }
    }

}
