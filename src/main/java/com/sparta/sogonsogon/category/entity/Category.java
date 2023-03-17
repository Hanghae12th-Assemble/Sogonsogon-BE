package com.sparta.sogonsogon.category.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 카테고리명

    @Column(nullable = false,unique = true)
    private String slug; // 카테고리 URL 경로를 위한 슬러그

//    @OneToOne(mappedBy = "category")
//    private List<Radio> radios

    @Builder
    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

}
