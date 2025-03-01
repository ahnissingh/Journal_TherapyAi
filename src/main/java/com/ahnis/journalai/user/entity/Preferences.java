package com.ahnis.journalai.user.entity;
import com.ahnis.journalai.user.enums.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Preferences {

    private ReportFrequency reportFrequency;
    private Language language;
    private ThemePreference themePreference;
    private SupportStyle supportStyle;
    private Integer age;
    private Gender gender;

}
