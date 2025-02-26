// Preferences.java
package com.ahnis.journalai.entity;

import com.ahnis.journalai.enums.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Preferences {

    private TherapyFrequency therapyFrequency;
    private Language language;
    private ThemePreference themePreference;
    private SupportStyle supportStyle;
    private Integer age;
    private Gender gender;

}
