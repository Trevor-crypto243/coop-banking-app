package ke.coop.bank.service;

import ke.coop.bank.dto.TransferRequest;
import ke.coop.bank.dto.TransferResponse;

public interface TransferService {

    TransferResponse transfer(TransferRequest request);
}
