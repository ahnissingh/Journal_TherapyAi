package com.ahnis.journalai.user.repository;

import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ '$or' : [ { 'username' : ?0 }, { 'email' : ?0 } ] }")
    Optional<User> findByUsernameOrEmail(String identifier);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);


    //todo make /api/users/me/preferences PUT API
    //todo as pref will be made at registration we need only put
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
    @Update("{ '$set' : { 'password' : ?1 } }")
    long updatePassword(String userId, String password);


}
