package hu.webuni.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hu.webuni.tokenlib.JwtService;

@SpringBootApplication(scanBasePackageClasses = {JwtService.class, UserServiceApplication.class}) //JwtService masik package-ben van, emiatt kell .. user-service/LoginController-be injektalok, de ezt nem latja Beankent, hiaba van rajta a @Service a JwtService-n a token-lib/JwtService osztalyon, de ez masik package (hu.webuni.tokenlib) .. de csak a hu.webuni.user es alpackage-eiben keresi
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
