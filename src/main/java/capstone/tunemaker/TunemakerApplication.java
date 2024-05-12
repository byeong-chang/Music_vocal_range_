package capstone.tunemaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TunemakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TunemakerApplication.class, args);
	}

}
