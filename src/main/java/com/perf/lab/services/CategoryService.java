package com.perf.lab.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.perf.lab.dtos.CreateCategoryRequestDto;
import com.perf.lab.exceptions.ResourceNotFoundException;
import com.perf.lab.repositories.CategoryRepository;
import com.perf.lab.schema.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequestDto requestDto) {
        Category newCategory = Category.builder()
            .name(requestDto.getName())
            .build();

        return categoryRepository.save(newCategory);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
        categoryRepository.delete(category);
        log.info("Category with id {} deleted successfully", id);
    }
    
}

