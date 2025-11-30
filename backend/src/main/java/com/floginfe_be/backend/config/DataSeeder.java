package com.floginfe_be.backend.config;

import com.floginfe_be.backend.entity.User;
import com.floginfe_be.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    public void run(String... args) throws Exception {
        String defaultUser1 = env.getProperty("CYPRESS_E2E_USER", "admin");
        String defaultUser2 = env.getProperty("CYPRESS_E2E_USER_2", "user");
        String password = env.getProperty("CYPRESS_E2E_PASS", "abc123");

        // create two default users for E2E: admin and user (or from env)
        createIfMissing(defaultUser1, password);
        createIfMissing(defaultUser2, password);
    }

    private void createIfMissing(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User u = new User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(password));
            userRepository.save(u);
            System.out.println("[DataSeeder] Created user '" + username + "' for E2E tests");
        } else {
            System.out.println("[DataSeeder] User '" + username + "' already exists");
        }
    }

}
