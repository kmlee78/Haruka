package com.programming.dmaker.service;

import com.programming.dmaker.dto.CreateDeveloper;
import com.programming.dmaker.dto.DeveloperDetailDto;
import com.programming.dmaker.dto.EditDeveloper;
import com.programming.dmaker.entity.Developer;
import com.programming.dmaker.exception.DMakerErrorCode;
import com.programming.dmaker.exception.DMakerException;
import com.programming.dmaker.repository.DeveloperRepository;
import com.programming.dmaker.repository.RetiredDeveloperRepository;
import com.programming.dmaker.type.DeveloperLevel;
import com.programming.dmaker.type.DeveloperSkillType;
import com.programming.dmaker.type.StatusCode;
import com.programming.dmaker.constant.DMakerConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {
    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private RetiredDeveloperRepository retiredDeveloperRepository;

    @InjectMocks
    private DMakerService dMakerService;

    private final Developer defaultDeveloper = Developer.builder()
            .developerLevel(DeveloperLevel.JUNIOR)
            .developerSkillType(DeveloperSkillType.FRONT_END)
            .experienceYears(7)
            .statusCode(StatusCode.EMPLOYED)
            .memberId("a")
            .name("jimmy")
            .age(28)
            .build();

    private  CreateDeveloper.Request getCreateRequest(
            DeveloperLevel developerLevel,
            DeveloperSkillType developerSkillType,
            Integer experienceYear
    ) {
        return CreateDeveloper.Request.builder()
                .developerLevel(developerLevel)
                .developerSkillType(developerSkillType)
                .experienceYears(experienceYear)
                .memberId("a")
                .name("jimmy")
                .age(28)
                .build();
    }

    private final EditDeveloper.Request defaultEditRequest = EditDeveloper.Request.builder()
            .developerLevel(DeveloperLevel.JUNIOR)
            .developerSkillType(DeveloperSkillType.FRONT_END)
            .experienceYears(5)
            .build();

    @Test
    public void testGetDeveloper() {
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        DeveloperDetailDto developerDetailDto = dMakerService.getDeveloperDetail("gg");

        Assertions.assertEquals(DeveloperLevel.JUNIOR, developerDetailDto.getDeveloperLevel());
        assertEquals(DeveloperSkillType.FRONT_END, developerDetailDto.getDeveloperSkillType());
        assertEquals(7, developerDetailDto.getExperienceYears());
    }

    @Test
    public void testCreateDeveloper_success() {
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.empty());
        ArgumentCaptor<Developer> captor =
                ArgumentCaptor.forClass(Developer.class);

        // when
        CreateDeveloper.Response developer = dMakerService.createDeveloper(getCreateRequest(
                DeveloperLevel.JUNIOR, DeveloperSkillType.FRONT_END, 7
        ));

        // then
        verify(developerRepository, times(1))
                .save(captor.capture());
        Developer savedDeveloper = captor.getValue();
        Assertions.assertEquals(DeveloperLevel.JUNIOR, savedDeveloper.getDeveloperLevel());
        assertEquals(DeveloperSkillType.FRONT_END, savedDeveloper.getDeveloperSkillType());
        assertEquals(7, savedDeveloper.getExperienceYears());
    }

    @Test
    public void testCreateDeveloper_failed_with_duplicated() {
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));
//        given(developerRepository.save(any()))
//                .willReturn(defaultDeveloper);

        // when
        // then
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(
                        DeveloperLevel.SENIOR, DeveloperSkillType.BACK_END, 12
                ))
        );
        Assertions.assertEquals(DMakerErrorCode.DUPLICATED_MEMBER_ID, dMakerException.getDMakerErrorCode());
    }

    @Test
    public void testCreateDeveloper_failed_with_unmatched_level() {
        // given
        // when
        // then
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(
                        DeveloperLevel.JUNIOR,
                        DeveloperSkillType.FRONT_END,
                        DMakerConstant.MAX_JUNIOR_EXPERIENCE_YEAR + 1
                ))
        );
        assertEquals(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED
                , dMakerException.getDMakerErrorCode());

        DMakerException dMakerException2 = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(
                        DeveloperLevel.JUNIOR,
                        DeveloperSkillType.FRONT_END,
                        DMakerConstant.MIN_JUNIOR_EXPERIENCE_YEAR - 1
                ))
        );
        assertEquals(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED
                , dMakerException2.getDMakerErrorCode());

        DMakerException dMakerException3 = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(
                        DeveloperLevel.SENIOR,
                        DeveloperSkillType.FRONT_END,
                        DMakerConstant.MAX_SENIOR_EXPERIENCE_YEAR + 1
                ))
        );
        assertEquals(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED
                , dMakerException3.getDMakerErrorCode());

        DMakerException dMakerException4 = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(
                        DeveloperLevel.SENIOR,
                        DeveloperSkillType.FRONT_END,
                        DMakerConstant.MIN_SENIOR_EXPERIENCE_YEAR - 1
                ))
        );
        assertEquals(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED
                , dMakerException4.getDMakerErrorCode());
    }

    @Test
    public void testEditDeveloper() {
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        // when
        DeveloperDetailDto developerDetailDto = dMakerService.editDeveloper(
                "a",defaultEditRequest
        );

        // then
        Assertions.assertEquals(DeveloperLevel.JUNIOR, developerDetailDto.getDeveloperLevel());
        assertEquals(DeveloperSkillType.FRONT_END, developerDetailDto.getDeveloperSkillType());
        assertEquals(5, developerDetailDto.getExperienceYears());
    }

    @Test
    public void testDeleteDeveloper() {
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        // when
        DeveloperDetailDto developer = dMakerService.deleteDeveloper("a");

        // then
        Assertions.assertEquals(StatusCode.RETIRED, developer.getStatusCode());
    }
}