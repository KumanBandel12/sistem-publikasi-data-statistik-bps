package com.bps.publikasistatistik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PublikasiStatistikApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublikasiStatistikApplication.class, args);
	}

}
