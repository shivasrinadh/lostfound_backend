package com.campus.lostfound.config;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
@SuppressWarnings("null")
public class DataSeeder implements CommandLineRunner {

        private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

        private final UserRepository userRepository;
        private final ItemRepository itemRepository;
        private final PasswordEncoder passwordEncoder;

        public DataSeeder(UserRepository userRepository,
                        ItemRepository itemRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.itemRepository = itemRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) {
                if (userRepository.count() > 0) {
                        log.info("Seed data already present — skipping.");
                        return;
                }

                // ── Users ──────────────────────────────────────────────────────
                Objects.requireNonNull(userRepository.save(User.builder()
                                .username("admin")
                                .email("admin@campus.edu")
                                .password(passwordEncoder.encode("admin123"))
                                .phone("9000000001")
                                .role(User.Role.ADMIN)
                                .build()));

                User alice = Objects.requireNonNull(userRepository.save(User.builder()
                                .username("alice")
                                .email("alice@campus.edu")
                                .password(passwordEncoder.encode("alice123"))
                                .phone("9000000002")
                                .role(User.Role.USER)
                                .build()));

                User bob = Objects.requireNonNull(userRepository.save(User.builder()
                                .username("bob")
                                .email("bob@campus.edu")
                                .password(passwordEncoder.encode("bob123"))
                                .phone("9000000003")
                                .role(User.Role.USER)
                                .build()));

                // ── Items ──────────────────────────────────────────────────────
                Objects.requireNonNull(itemRepository.save(Item.builder()
                                .title("Black Laptop Bag")
                                .description("Lost near the library entrance. Has a red sticker on the zip.")
                                .location("Main Library")
                                .category(Item.Category.BAGS)
                                .type(Item.ItemType.LOST)
                                .status(Item.Status.OPEN)
                                .dateOccurred(LocalDate.now().minusDays(2))
                                .reportedBy(alice)
                                .build()));

                Objects.requireNonNull(itemRepository.save(Item.builder()
                                .title("Samsung Galaxy S23")
                                .description("Found near the cafeteria. Screen cracked on the corner.")
                                .location("Central Cafeteria")
                                .category(Item.Category.ELECTRONICS)
                                .type(Item.ItemType.FOUND)
                                .status(Item.Status.OPEN)
                                .dateOccurred(LocalDate.now().minusDays(1))
                                .reportedBy(bob)
                                .build()));

                Objects.requireNonNull(itemRepository.save(Item.builder()
                                .title("Blue Water Bottle")
                                .description("Nalgene bottle, blue with campus stickers.")
                                .location("Sports Complex - Gym")
                                .category(Item.Category.OTHER)
                                .type(Item.ItemType.LOST)
                                .status(Item.Status.OPEN)
                                .dateOccurred(LocalDate.now())
                                .reportedBy(alice)
                                .build()));

                Objects.requireNonNull(itemRepository.save(Item.builder()
                                .title("Student ID Card")
                                .description("Found on the ground near Block B. Name: Rahul K.")
                                .location("Block B Corridor")
                                .category(Item.Category.DOCUMENTS)
                                .type(Item.ItemType.FOUND)
                                .status(Item.Status.OPEN)
                                .dateOccurred(LocalDate.now())
                                .reportedBy(bob)
                                .build()));

                log.info("===== Seed Data Loaded =====");
                log.info("  Admin  → username: admin  / password: admin123");
                log.info("  User 1 → username: alice  / password: alice123");
                log.info("  User 2 → username: bob    / password: bob123");
                log.info("  H2 Console → http://localhost:8080/h2-console");
                log.info("  JDBC URL   → jdbc:h2:mem:lostfounddb");
        }
}
