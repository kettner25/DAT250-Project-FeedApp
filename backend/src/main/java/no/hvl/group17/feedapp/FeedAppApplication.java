package no.hvl.group17.feedapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FeedappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedappApplication.class, args);
	}

}
