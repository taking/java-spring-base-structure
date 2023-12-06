package kr.taking.backend.configuration;


import kr.taking.backend.model.OrgEntity;
import kr.taking.backend.model.RoleEntity;
import kr.taking.backend.model.UserEntity;
import kr.taking.backend.repository.OrgRepository;
import kr.taking.backend.repository.RoleRepository;
import kr.taking.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class Initalizer implements ApplicationRunner {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrgRepository orgRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {


        if(roleRepository.findByName("ROLE_ADMIN").isEmpty()) {

            RoleEntity roleEntity = RoleEntity.builder()
                .name("ROLE_ADMIN")
                .build();
            
            roleRepository.save(roleEntity);
        }
        

        if(roleRepository.findByName("ROLE_USER").isEmpty()) {

            RoleEntity roleEntity = RoleEntity.builder()
                .name("ROLE_USER")
                .build();

            roleRepository.save(roleEntity);
        }
        
        if(orgRepository.findByName("DEFAULT").isEmpty()) {

            Instant instant = Instant.now();
            OrgEntity orgEntity = OrgEntity.builder()
                    .name("DEFAULT")
                    .biznum("123-45-67890")
                    .contact("02-0000-0000")
                    .enabled(true)
                    .created_at(instant)
                    .build();

            orgRepository.save(orgEntity);
        }
        
        if(userRepository.findByuserid("admin").isEmpty()) {

            RoleEntity roleEntity = roleRepository.findByName("ROLE_ADMIN").get();
            
            userRepository.save(UserEntity.builder()
                .userid("admin")
                .username("관리자")
                .password(passwordEncoder.encode("admin"))
                .email("admin@test.com")
                .role(roleEntity)
                .enabled(true)
                .build());
        }
    }
}