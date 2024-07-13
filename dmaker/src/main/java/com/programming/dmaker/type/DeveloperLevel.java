package com.programming.dmaker.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.programming.dmaker.constant.DMakerConstant.*;


@AllArgsConstructor
@Getter
public enum DeveloperLevel {
    NEW("신입 개발자", 0, 0),
    JUNIOR("주니어 개발자", MIN_JUNIOR_EXPERIENCE_YEAR, MAX_JUNIOR_EXPERIENCE_YEAR),
    SENIOR("시니어 개발자", MIN_SENIOR_EXPERIENCE_YEAR, MAX_SENIOR_EXPERIENCE_YEAR);

    private final String description;
    private final Integer minExperienceYear;
    private final Integer maxExperienceYear;
}
