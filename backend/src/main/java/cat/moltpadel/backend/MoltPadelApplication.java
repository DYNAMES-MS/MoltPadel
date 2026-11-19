package cat.moltpadel.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MoltPadelApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoltPadelApplication.class, args);
	}

}
