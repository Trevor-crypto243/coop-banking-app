package ke.coop.bank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
class BankApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void transferMovesMoneyBetweenAccounts() throws Exception {
        String jane = createCustomer("Jane", "11111111", "jane@test.com", "10000");
        String john = createCustomer("John", "22222222", "john@test.com", "500");

        mvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccount": "%s", "toAccount": "%s", "amount": 2500}
                                """.formatted(jane, john)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fromAccountBalance").value(7500.0))
                .andExpect(jsonPath("$.data.toAccountBalance").value(3000.0));

        mvc.perform(get("/api/v1/accounts/{acc}/balance", john))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.formattedBalance").value("KES 3,000.00"));
    }

    @Test
    void transferFailsWhenBalanceIsNotEnough() throws Exception {
        String a = createCustomer("Mary", "33333333", "mary@test.com", "100");
        String b = createCustomer("Peter", "44444444", "peter@test.com", "0");

        mvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccount": "%s", "toAccount": "%s", "amount": 5000}
                                """.formatted(a, b)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("FAILED"));

        // nothing should have moved
        mvc.perform(get("/api/v1/accounts/{acc}/balance", a))
                .andExpect(jsonPath("$.data.balance").value(100.0));
    }

    @Test
    void invalidCustomerIsRejected() throws Exception {
        mvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName": "", "nationalId": "12", "email": "not-an-email", "openingBalance": -5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.openingBalance").exists());
    }

    @Test
    void unknownAccountReturns404() throws Exception {
        mvc.perform(get("/api/v1/accounts/0100000000000/balance"))
                .andExpect(status().isNotFound());
    }

    private String createCustomer(String name, String nationalId, String email, String openingBalance) throws Exception {
        String body = """
                {"firstName": "%s", "lastName": "Test", "nationalId": "%s", "email": "%s",
                 "phoneNumber": "0712345678", "openingBalance": %s}
                """.formatted(name, nationalId, email, openingBalance);

        String json = mvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = mapper.readTree(json);
        return node.at("/data/accountNumber").asText();
    }
}
