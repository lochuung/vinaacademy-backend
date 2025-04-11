package com.vinaacademy.platform.feature.enrollment.mapper;

import com.vinaacademy.platform.feature.enrollment.Enrollment;
import com.vinaacademy.platform.feature.enrollment.dto.EnrollmentProgressDto;
import org.mapstruct.Mapper;

@Mapper
public interface EnrollmentMapper {
    EnrollmentMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(EnrollmentMapper.class);

    EnrollmentProgressDto toDto(Enrollment courseEnrollment);
}
