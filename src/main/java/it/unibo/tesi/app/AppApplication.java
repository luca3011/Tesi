package it.unibo.tesi.app;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableScheduling
@RestController
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	private String data;

	@Scheduled(fixedDelay = 500)
	public void getStatus(){
		data = "Applicazione attiva, ultima sincronizzazione: " + new Date();
	}

	@GetMapping(value="/stato")
	public String getMethodName() {
		return data;
	}
	


}
