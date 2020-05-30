package home.bruce.webfluxPractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class WebfluxPracticeApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(WebfluxPracticeApplication.class, args);
	}
}
