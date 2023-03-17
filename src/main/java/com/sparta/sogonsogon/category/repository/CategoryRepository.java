package com.sparta.sogonsogon.category.repository;

import com.sparta.sogonsogon.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
//    List<Category> findAllByNameContainingIgnoreCase(String name);
    Category findBySlug(String slug);

}
