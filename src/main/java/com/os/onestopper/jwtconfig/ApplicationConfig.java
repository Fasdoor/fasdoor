package com.os.onestopper.jwtconfig;

import com.os.onestopper.common.CommonBeans;
import com.os.onestopper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final CommonBeans commonBeans;

    public ApplicationConfig(UserRepository userRepository, CommonBeans commonBeans) {
        this.userRepository = userRepository;
        this.commonBeans = commonBeans;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (username.contains("@")) {
                    return userRepository.findByEmailId(username).orElseThrow(() -> new UsernameNotFoundException("Invalid Email Id"));
                } else {
                    return userRepository.findByPhoneNumber(username).orElseThrow(() -> new UsernameNotFoundException("Invalid Mobile Number"));
                }
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(commonBeans.passwordEncoder());
        return authProvider;
    }
}
