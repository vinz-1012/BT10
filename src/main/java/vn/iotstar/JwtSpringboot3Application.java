package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JwtSpringboot3Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JwtSpringboot3Application.class);
        app.addInitializers(new vn.iotstar.configs.DatabaseInitializer());
        app.run(args);
    }

}
