    package com.lazycode.lazyhotel.service;

    import com.lazycode.lazyhotel.exception.UserAlreadyExistsException;
    import com.lazycode.lazyhotel.model.Role;
    import com.lazycode.lazyhotel.model.User;
    import com.lazycode.lazyhotel.repository.RoleRepository;
    import com.lazycode.lazyhotel.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import jakarta.annotation.PostConstruct;

    import java.util.Collections;
    import java.util.List;


    @Service
    @RequiredArgsConstructor
    //room service implementor
    public class UserService implements IUserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;

        @Override
        public User registerUser(User user) {
            if(userRepository.existsByEmail(user.getEmail())){
                throw new UserAlreadyExistsException(user.getEmail() + "already exists");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role 'ROLE_USER' not found"));
            user.setRoles(Collections.singletonList(userRole));
            return userRepository.save(user);
        }

        @PostConstruct
        public void initDefaultRoles() {
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role roleUser = new Role();
                roleUser.setName("ROLE_USER");
                roleRepository.save(roleUser);
            }
        }


        @Override
        public List<User> getUsers() {
            return userRepository.findAll();
        }

        @Transactional
        @Override
        public void deleteUser(String email) {
            User theUser = getUser(email);
            if (theUser != null){
                userRepository.deleteByEmail(email);
            }

        }

        @Override
        public User getUser(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
    }
