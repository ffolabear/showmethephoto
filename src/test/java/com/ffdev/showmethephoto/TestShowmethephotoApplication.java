package com.ffdev.showmethephoto;

import org.springframework.boot.SpringApplication;

public class TestShowmethephotoApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShowmethephotoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
