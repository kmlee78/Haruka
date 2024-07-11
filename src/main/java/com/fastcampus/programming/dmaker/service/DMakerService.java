package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.StatusCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {
        validateCreateDeveloperRequest(request);

        Developer developer = createDeveloperFromRequest(request);
        developerRepository.save(developer);
        return CreateDeveloper.Response.fromEntity(developer);
    }

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
        return Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .name(request.getName())
                .age(request.getAge())
                .statusCode(StatusCode.EMPLOYED)
                .build();
    }

    private void validateCreateDeveloperRequest(@NonNull CreateDeveloper.Request request) {
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent(developer -> {
                    throw new DMakerException(DMakerErrorCode.DUPLICATED_MEMBER_ID);
                });
    }

    @Transactional
    public List<DeveloperDto> getAllDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
    }


    private Developer getDeveloperByMemberId(String memberId) {
        return developerRepository.findByMemberId(memberId).orElseThrow(
            () -> new DMakerException(DMakerErrorCode.NO_DEVELOPER)
        );
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());

        Developer developer = getUpdatedDeveloperFromRequest(request, getDeveloperByMemberId(memberId));
        return DeveloperDetailDto.fromEntity(developer);
    }

    private static Developer getUpdatedDeveloperFromRequest(
            EditDeveloper.Request request, Developer developer
    ) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());
        return developer;
    }

    private void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        if (experienceYears < developerLevel.getMinExperienceYear()
                || experienceYears > developerLevel.getMaxExperienceYear()) {
            throw new DMakerException(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // EMPLOYED -> RETIRED
        Developer developer = getDeveloperByMemberId(memberId);
        developer.setStatusCode(StatusCode.RETIRED);

        // Save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);

        return DeveloperDetailDto.fromEntity(developer);
    }
}
