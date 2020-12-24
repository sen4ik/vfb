package com.sen4ik.vfb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication
//@EnableScheduling
//public class VfbApplication extends SpringBootServletInitializer {
//
//	public static void main(String[] args) {
//		SpringApplication.run(VfbApplication.class, args);
//	}
//
//}

@SpringBootApplication
@EnableScheduling
public class VfbApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(VfbApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(VfbApplication.class);
	}
}

