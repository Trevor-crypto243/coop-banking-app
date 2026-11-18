package ke.coop.bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bankApi() {
        return new OpenAPI().info(new Info()
                .title("Co-op Banking API")
                .description("Customer onboarding, funds transfer and balance enquiry")
                .version("1.0"));
    }
}
