package com.ahnis.journalai.enums;

import com.ahnis.journalai.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;


@Getter
public enum TherapistType {
    FRIENDLY("Use warm, approachable language. Focus on building trust and rapport."),
    REALISTIC("Provide practical, evidence-based advice. Avoid sugarcoating."),
    MOTIVATIONAL("Offer encouraging, uplifting responses. Highlight strengths and progress."),
    MINDFUL("Emphasize present-moment awareness. Use grounding techniques."),
    ANALYTICAL("Break down problems logically. Offer structured approaches."),
    COMPASSIONATE("Show deep empathy and understanding. Validate feelings."),
    HUMOROUS("Use appropriate humor to lighten mood. Maintain professionalism."),
    EXISTENTIAL("Explore deeper meaning and purpose. Ask reflective questions.");

    private final String description;

    TherapistType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static TherapistType from(String value) {
        return EnumUtils.fromString(TherapistType.class, value);
    }
}
