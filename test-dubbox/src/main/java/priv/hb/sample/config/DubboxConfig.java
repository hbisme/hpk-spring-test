package priv.hb.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
@ImportResource(
        {"classpath:dubbox/hirac-consumer.xml", "classpath:dubbox/hirac-register.xml", "classpath:dubbox/hirac-provider.xml"}
)
public class DubboxConfig {
}
