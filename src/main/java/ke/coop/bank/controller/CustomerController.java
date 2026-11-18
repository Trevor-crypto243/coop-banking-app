package ke.coop.bank.controller;

import jakarta.validation.Valid;
import ke.coop.bank.dto.ApiResponse;
import ke.coop.bank.dto.CustomerRequest;
import ke.coop.bank.dto.CustomerResponse;
import ke.coop.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    //customer controller
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerRequest request) {
        log.info("POST /api/v1/customers");
        CustomerResponse customer = customerService.createCustomer(request);
        String message = "Welcome " + request.getFirstName() + "! Your account " + customer.getAccountNumber() + " is ready.";
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("SUCCESS", message, customer));
    }
}
