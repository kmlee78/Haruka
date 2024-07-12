package com.programming.dmaker.dto;

import com.programming.dmaker.type.DeveloperLevel;
import com.programming.dmaker.type.DeveloperSkillType;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class EditDeveloper {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        private DeveloperLevel developerLevel;
        @NotNull
        private DeveloperSkillType developerSkillType;
        @NotNull
        @Min(0)
        @Max(20)
        private Integer experienceYears;
    }
}
