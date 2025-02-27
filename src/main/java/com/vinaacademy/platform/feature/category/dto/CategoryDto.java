package com.vinaacademy.platform.feature.category.dto;

import com.vinaacademy.platform.feature.common.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto extends BaseDto {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;

    List<CategoryDto> children;
}
