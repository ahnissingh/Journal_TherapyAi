package com.ahnis.journalai;

import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.TimeZone;

import static com.ahnis.journalai.user.enums.ReportFrequency.*;

@SpringBootApplication
@EnableMongoAuditing
@ConfigurationPropertiesScan
/**
 *
 * todo 1 ) Use mapping libraries instead of custom mappers
 * todo 2 ) When finalised properties files add the packages here for better
 * todo 3 ) Consider defining a token batching strategy for the project (Most required in our use case as journals need batching)
 * todo 4 ) Consider defining all exception messages in yaml file for uniformity
 *
 * https://docs.spring.io/spring-ai/reference/api/vectordbs.html
 */
public class JournalAi2Application {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);
    }
}
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//class InitializeNextReportOnForExistingUsers {
//    private final UserRepository userRepository;
//
//    @Bean
//    public CommandLineRunner init() {
//        return args -> {
//            var users = userRepository.findAll();
//            users.forEach(user -> {
//                if (user.getPreferences() != null && user.getPreferences().getReportFrequency() != null) {
//                    LocalDate nextReportOn = calculateNextReportOn(user.getCreatedAt().toLocalDate(), user.getPreferences().getReportFrequency());
//                    user.setNextReportOn(nextReportOn);
//                    userRepository.save(user);
//                }
//            });
//            log.info("Updated nextReport on for all users------------------------------------------------------------------------------------------");
//        };
//    }
//
//    private LocalDate calculateNextReportOn(LocalDate createdAt, ReportFrequency reportFrequency) {
//        return switch (reportFrequency) {
//            case WEEKLY -> createdAt.plusDays(7);
//            case BIWEEKLY -> createdAt.plusDays(14);
//            case MONTHLY -> createdAt.plusMonths(1);
//        };
//    }
//}




