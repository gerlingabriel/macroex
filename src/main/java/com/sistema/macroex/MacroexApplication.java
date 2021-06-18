package com.sistema.macroex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MacroexApplication{

	public static void main(String[] args) {
		SpringApplication.run(MacroexApplication.class, args);

		// String senha = new BCryptPasswordEncoder().encode("123");
		// System.out.println(senha); 
	}

}
