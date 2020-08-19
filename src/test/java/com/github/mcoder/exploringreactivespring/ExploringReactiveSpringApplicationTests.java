package com.github.mcoder.exploringreactivespring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Slf4j
class ExploringReactiveSpringApplicationTests {

	@Test
	void contextLoads() {
		String currentSalt = BCrypt.gensalt();
		String randompass = BCrypt.hashpw("randompass", currentSalt);
		log.info("salt is : {} , password is : {}", currentSalt, randompass);
		boolean checkpass = BCrypt.checkpw("randompass", randompass);
		log.info("pass check was successful : {}", checkpass);
	}

}
