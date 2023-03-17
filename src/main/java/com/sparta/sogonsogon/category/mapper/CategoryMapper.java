//package com.sparta.sogonsogon.category.mapper;
//
//import com.sparta.sogonsogon.category.dto.CategoryRequestDto;
//import com.sparta.sogonsogon.category.dto.CategoryResponseDto;
//import com.sparta.sogonsogon.category.entity.Category;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class CategoryMapper {
//    public CategoryResponseDto toResponseDto(Category category) {
//        CategoryResponseDto responseDto = new CategoryResponseDto();
//        responseDto.setId(category.getId());
//        responseDto.setName(category.getName());
//        responseDto.setSlug(category.getSlug());
//        return responseDto;
//    }
//
//    public Category toEntity(CategoryRequestDto requestDto) {
//        Category category = new Category();
//        category.setName(requestDto.getName());
//        category.setSlug(requestDto.getSlug());
//        return category;
//    }
//}
