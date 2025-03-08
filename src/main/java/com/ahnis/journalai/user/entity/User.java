package com.ahnis.journalai.user.entity;

import com.ahnis.journalai.user.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;
    @JsonIgnore
    @ToString.Exclude
    private String password;

    private Preferences preferences;

  
    @Indexed
    private Instant nextReportOn;

    @Indexed
    private Instant lastReportAt;

    private String timezone; // New field

    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    //new fields added
    @Builder.Default
    private boolean enabled = true; // Default to true

    @Builder.Default
    private boolean accountNonLocked = true; // Default to true

    @Builder.Default
    private boolean accountNonExpired = true; // Default to true

    @Builder.Default
    private boolean credentialsNonExpired = true; // Default to true

    @CreatedDate

    private Instant createdAt;

    @LastModifiedDate

    private Instant updatedAt;


    private int currentStreak; // Current consecutive days of journal writing
    private int longestStreak; // Longest streak achieved
    private Instant lastJournalEntryDate; // Date of the last journal entry

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
}
