package com.example.careercraft.repository;

import com.example.careercraft.entity.Category;
import feign.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByName(@Param("name") String name);

    @NotNull Optional<Category> findById(@NotNull Long id);;
}
