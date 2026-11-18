package ke.coop.bank.controller;

import jakarta.validation.Valid;
import ke.coop.bank.dto.ApiResponse;
import ke.coop.bank.dto.CustomerRequest;
import ke.coop.bank.dto.CustomerResponse;
import ke.coop.bank.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //customer controller
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse customer = customerService.createCustomer(request);
        String message = "Welcome " + request.firstName() + "! Your account " + customer.accountNumber() + " is ready.";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(message, customer));
    }
}
