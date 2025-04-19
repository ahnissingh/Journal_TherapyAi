
package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
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
    private final CaffeineCacheManager cacheManager;


    @Override
    @Cacheable(value = "userDetails", key = "#identifier")
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Looking up user with identifier '{}'", identifier);
        Cache cache = cacheManager.getCache("userDetails");
        if (cache != null && cache.get(identifier) != null) {
            log.debug("Cache hit for '{}'", identifier);
        }
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

    @CacheEvict(value = "userDetails", key = "#identifier")
    public void evictUserCache(String identifier) {
        log.info("Evicting cache for user: {}", identifier);
    }
}


