package ke.coop.bank.service;

import ke.coop.bank.dto.BalanceResponse;

public interface AccountService {

    BalanceResponse getBalance(String accountNumber);
}
