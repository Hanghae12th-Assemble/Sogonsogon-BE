package com.sparta.sogonsogon.radio.repository;

import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.radio.entity.Radio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RadioRepository extends JpaRepository<Radio, Long> {
    Optional<Radio> findByTitle(String title);

    Page<Radio> findAll(Pageable pageable);

    List<Radio> findByTitleContaining(String title);

    Page<Radio> findAllByCategoryType(CategoryType categoryType,
                                      Pageable pageable);

    Optional<Radio> findById(Long radioId);
}
