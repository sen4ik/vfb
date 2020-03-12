package com.sen4ik.vfb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VfbApplication {

	public static void main(String[] args) {
		SpringApplication.run(VfbApplication.class, args);
	}

}

