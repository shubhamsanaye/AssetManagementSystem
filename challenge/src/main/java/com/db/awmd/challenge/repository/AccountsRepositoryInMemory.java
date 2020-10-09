package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotPresentException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.IncorrectInputException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	public AccountsRepositoryInMemory() {
		accounts.put("SBI1", new Account("SBI1",new BigDecimal(568465.67)));
		accounts.put("SBI2", new Account("SBI2",new BigDecimal(11065.67)));
		accounts.put("SBI3", new Account("SBI3",new BigDecimal(1197665.67)));
		accounts.put("SBI4", new Account("SBI4",new BigDecimal(24765.67)));
	}

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException(
					"Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public void moneyTransferRequest(String accountFromID, String accountToID, BigDecimal amount) {
		if(amount.intValue()<0) {
			throw new IncorrectInputException("Amount should be Positive");
		}
		if(!accounts.containsKey(accountFromID)||(!accounts.containsKey(accountToID))){
			throw new AccountNotPresentException("Enter Correct Account ID");
		}
		Account AccountFrom=accounts.get(accountFromID);
		Account AccountTo=accounts.get(accountToID);

		BigDecimal FutureAccountFromBalance=AccountFrom.getBalance().subtract(amount);


		if((FutureAccountFromBalance.intValue()>0)) {
			AccountFrom.setBalance(AccountFrom.getBalance().subtract(amount));
			AccountTo.setBalance(AccountTo.getBalance().add(amount));
			accounts.put(accountFromID, AccountFrom);
			accounts.put(accountToID, AccountTo);
		}
		else {
			throw new InsufficientBalanceException("Insufficient Balance");
		}
	}

}
