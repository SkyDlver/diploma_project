package com.example.kooking.utils;

import com.example.kooking.dto.*;
import com.example.kooking.model.Review;
import com.example.kooking.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RecipeMapper.class})
public interface ReviewMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "recipe", source = "recipe")
    ReviewDto reviewToReviewDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    Review createReviewDtoToReview(CreateReviewDto createReviewDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    void updateReviewFromDto(UpdateReviewDto updateReviewDto, @MappingTarget Review review);
}