package ke.coop.bank.service;

import ke.coop.bank.dto.CustomerRequest;
import ke.coop.bank.dto.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);
}
