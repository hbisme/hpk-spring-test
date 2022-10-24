package priv.hb.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:mq/bean-${mq.env}.xml")
// @ImportResource("classpath:bean.xml")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}



