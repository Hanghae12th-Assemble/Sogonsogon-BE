package com.sparta.sogonsogon.category.service;

import com.sparta.sogonsogon.category.dto.CategoryRequestDto;
import com.sparta.sogonsogon.category.dto.CategoryResponseDto;
import com.sparta.sogonsogon.category.entity.Category;
import com.sparta.sogonsogon.category.repository.CategoryRepository;
import com.sparta.sogonsogon.enums.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;


    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = Category.builder()
                .name(requestDto.getName())
                .slug(slugify(requestDto.getName()))
                .build();
        categoryRepository.save(category);

        return new CategoryResponseDto(category);

    }

    @Transactional
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> list = categoryRepository.findAll();
        List<CategoryResponseDto> categoryResponseDtos = new ArrayList<>();
        for (Category category:list){
            categoryResponseDtos.add(new CategoryResponseDto(category));
        }
        return categoryResponseDtos;
    }

    @Transactional
    public CategoryResponseDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);

        if (category == null) {
            throw new IllegalArgumentException(ErrorMessage.NOT_FOUND_CATEGORY.getMessage());
        }
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(category.getId());
        categoryResponseDto.setName(category.getName());
        categoryResponseDto.setSlug(category.getSlug());

        return categoryResponseDto;
    }



    private String slugify(String name) {
        return name.trim().toLowerCase().replaceAll("\\s+", "-");
    }



}
