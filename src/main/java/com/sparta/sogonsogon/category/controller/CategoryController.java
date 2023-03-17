package com.sparta.sogonsogon.category.controller;

import com.sparta.sogonsogon.category.dto.CategoryRequestDto;
import com.sparta.sogonsogon.category.dto.CategoryResponseDto;
import com.sparta.sogonsogon.category.service.CategoryService;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {


    private final CategoryService categoryService;

    @PostMapping("/category")
    public StatusResponseDto<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto requestDto) {

        return StatusResponseDto.success(HttpStatus.OK,categoryService.createCategory(requestDto));
    }

    @GetMapping("/")
    public StatusResponseDto<List<CategoryResponseDto>> getAllCategories() {

        return  StatusResponseDto.success(HttpStatus.OK,categoryService.getAllCategories());
    }

    @GetMapping("/{slug}")
    public StatusResponseDto<CategoryResponseDto> getCategoryBySlug(@PathVariable String slug) {

        return StatusResponseDto.success(HttpStatus.OK,categoryService.getCategoryBySlug(slug));
    }

}
