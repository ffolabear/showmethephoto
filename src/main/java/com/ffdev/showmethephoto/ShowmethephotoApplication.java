package com.ffdev.showmethephoto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

import static java.lang.System.getenv;

@SpringBootApplication
public class ShowmethephotoApplication {

    public static void main(String[] args) {
        Map<String, String> env = getenv();
        SpringApplication.run(ShowmethephotoApplication.class, args);
    }

}
