// Preferences.java
package com.ahnis.journalai.entity;

import com.ahnis.journalai.enums.Language;
import com.ahnis.journalai.enums.ThemePreference;
import com.ahnis.journalai.enums.TherapyFrequency;
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

}
