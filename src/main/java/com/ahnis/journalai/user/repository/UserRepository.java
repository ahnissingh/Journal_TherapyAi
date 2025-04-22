package com.ahnis.journalai.user.repository;

import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query(value = "{'_id' : ?0}", fields = "{'therapistId' : 1}")
    Optional<TherapistIdProjection> findTherapistIdById(String id);

    @Query("{ '$or' : [ { 'username' : ?0 }, { 'email' : ?0 } ] }")
    Optional<User> findByUsernameOrEmail(String identifier);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'preferences' : ?1 } }")
    void updatePreferences(String userId, Preferences preferences);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'enabled' : ?1 } }")
    long updateEnabledStatus(String userId, boolean enabled);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'accountNonLocked' : ?1 } }")
    long updateAccountNonLockedStatus(String userId, boolean accountNonLocked);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'email' : ?1 } }")
    long updateEmail(String userId, String email);

    @Query("{ '_id' : ?0 }")
    @Transactional
    @Update("{ '$set' : { 'password' : ?1 } }")
    long updatePassword(String userId, String password);


    @Query("{ 'username' : ?0 }")
    @Update("{ '$set' : { 'password' : ?1 } }")
    long updatePasswordByUsername(String username, String password);

    @Query("{ 'username' : ?0 }")
    @Update("{ '$set' : { 'email' : ?1 } }")
    long updateEmailByUsername(String username, String email);

    @Query("{ 'nextReportOn' : { $gte: ?0, $lte: ?1 } }")
    List<User> findByNextReportOn(Instant startOfDay, Instant endOfDay);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'nextReportOn' : ?1 } }")
    void updateNextReportOnById(String userId, Instant nextReportOn);

    @Transactional
    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'lastReportAt' : ?1 } }")
    void updateLastReportAtById(String userId, Instant lastReportAt);

    @Transactional
    @Query("{ 'username' : ?0 }")
    @Update("{ '$set' : { 'nextReportOn' : ?1 } }")
    void updateByUsernameAndNextReportOn(String username, Instant nextReportOn);

    @Query(value = "{ 'username' : ?0 }", delete = true)
    long deleteByUsername(String username);

    @Query("{ 'username' : ?0 }")
    @Update("{ '$set' : { 'preferences' : ?1 } }")
    long updatePreferencesByUsername(String username, Preferences preferences);


    @Query("{ 'username' : ?0 }")
    @Update("{ '$set' : { 'nextReportOn' : ?1 } }")
    long updateNextReportOnByUsername(String username, Instant nextReportOn);


    @Query("{ 'preferences.remindersEnabled': ?0 }")
    List<User> findByRemindersEnabled(boolean remindersEnabled);


    Page<User> findAllByIdIn(Set<String> userIds, Pageable pageable);

    boolean existsByUsername(String username);

}
