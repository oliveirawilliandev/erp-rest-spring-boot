package br.com.willian.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    PREFER_NOT_TO_SAY;

    @JsonCreator
    public static Gender fromValue(String text) {
        if (text == null) return null;

        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(text)) {
                return gender;
            }
        }
        throw new IllegalArgumentException(
                "Invalid gender value: " + text +
                        ". Allowed values: MALE, FEMALE, NON_BINARY, PREFER_NOT_TO_SAY."
        );

    }
}




