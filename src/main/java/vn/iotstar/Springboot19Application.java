package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@SpringBootApplication
public class Springboot19Application {

    public static void main(String[] args) {
        SpringApplication.run(Springboot19Application.class, args);
    }

    @Bean
    CommandLineRunner init(
        RoleRepository roleRepository,
        UserRepository userRepository,
        vn.iotstar.repository.ProductRepository productRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Role userRole = roleRepository
                .findByName("ROLE_USER")
                .orElseGet(() ->
                    roleRepository.save(
                        Role.builder()
                            .name("ROLE_USER")
                            .build()
                    )
                );

            Role adminRole = roleRepository
                .findByName("ROLE_ADMIN")
                .orElseGet(() ->
                    roleRepository.save(
                        Role.builder()
                            .name("ROLE_ADMIN")
                            .build()
                    )
                );

            User user = userRepository.findByUsername("user01").orElseGet(() -> {
                User u = User.builder()
                    .username("user01")
                    .email("user01@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Huỳnh Cao Trung Đức")
                    .images("/images/user.png")
                    .role(userRole)
                    .enabled(true)
                    .build();
                return userRepository.save(u);
            });

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                    .username("admin")
                    .email("admin@hcmute.edu.vn")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("System Administrator")
                    .images("/images/admin.png")
                    .role(adminRole)
                    .enabled(true)
                    .build();
                userRepository.save(admin);
            }

            if (productRepository.count() == 0) {
                productRepository.save(vn.iotstar.entity.Product.builder()
                    .name("Điện thoại Oppo A95")
                    .description("Điện thoại thông minh Oppo A95 cấu hình cao")
                    .price(new java.math.BigDecimal("6565656.00"))
                    .imageUrl("/images/user.png")
                    .user(user)
                    .build());
                productRepository.save(vn.iotstar.entity.Product.builder()
                    .name("Điện thoại Oppo A6")
                    .description("Điện thoại Oppo A6 chính hãng")
                    .price(new java.math.BigDecimal("689990.00"))
                    .imageUrl("/images/user.png")
                    .user(user)
                    .build());
            }
        };
    }
}
