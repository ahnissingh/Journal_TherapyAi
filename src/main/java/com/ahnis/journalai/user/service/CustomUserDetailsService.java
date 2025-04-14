
package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByUsernameOrEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));
//
//    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(identifier);
        if (user.isPresent()) {
            return user.get(); // Safe after isPresent() check
        }
        Optional<Therapist> therapist = therapistRepository.findByUsernameOrEmail(identifier);
        if (therapist.isPresent()) {
            return therapist.get(); // Safe after isPresent() check
        }
        throw new UsernameNotFoundException("Not found: " + identifier);
    }
}


