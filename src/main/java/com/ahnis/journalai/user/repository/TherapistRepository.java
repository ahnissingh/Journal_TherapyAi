package com.ahnis.journalai.user.repository;

import com.ahnis.journalai.user.entity.Therapist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TherapistRepository extends MongoRepository<Therapist, String> {

    @Query("{ '$or' : [ { 'username' : ?0 }, { 'email' : ?0 } ] }")
    Optional<Therapist> findByUsernameOrEmail(String identifier);

    boolean existsByUsernameOrEmail(String username, String email);
}
