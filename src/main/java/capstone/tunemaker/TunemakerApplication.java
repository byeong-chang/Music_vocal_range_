package capstone.tunemaker;

import capstone.tunemaker.service.TokenBlacklistService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class TunemakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TunemakerApplication.class, args);
	}

}
