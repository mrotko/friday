package pl.rotkom.friday;

import org.springframework.boot.SpringApplication;

public class TestFridayApplication {

    public static void main(String[] args) {
        SpringApplication.from(FridayApp::main).with(TestcontainersConfiguration.class).run(args);
    }

}
