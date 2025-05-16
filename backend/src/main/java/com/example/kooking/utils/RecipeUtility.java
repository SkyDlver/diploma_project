package com.example.kooking.utils;

import com.example.kooking.enums.CuisineType;
import com.example.kooking.enums.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Component
public class RecipeUtility {

    /**
     * Determines the current meal type based on time of day
     */
    public MealType getCurrentMealType() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        if (hour >= 5 && hour < 11) {
            return MealType.BREAKFAST;
        } else if (hour >= 11 && hour < 15) {
            return MealType.LUNCH;
        } else if (hour >= 15 && hour < 18) {
            return MealType.SNACK;
        } else {
            return MealType.DINNER;
        }
    }

    /**
     * Determines the current season based on the month
     */
    public Season getCurrentSeason() {
        Month currentMonth = LocalDate.now().getMonth();

        switch (currentMonth) {
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
                return Season.WINTER;
            case MARCH:
            case APRIL:
            case MAY:
                return Season.SPRING;
            case JUNE:
            case JULY:
            case AUGUST:
                return Season.SUMMER;
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                return Season.FALL;
            default:
                return Season.SUMMER;
        }
    }

    /**
     * Gets cuisines associated with a season
     */
    public List<CuisineType> getSeasonalCuisines(Season season) {
        switch (season) {
            case WINTER:
                return List.of(CuisineType.RUSSIAN, CuisineType.GERMAN, CuisineType.FRENCH, CuisineType.BRITISH);
            case SPRING:
                return List.of(CuisineType.MEDITERRANEAN, CuisineType.JAPANESE, CuisineType.GREEK, CuisineType.VIETNAMESE);
            case SUMMER:
                return List.of(CuisineType.MEXICAN, CuisineType.SPANISH, CuisineType.THAI, CuisineType.ITALIAN);
            case FALL:
                return List.of(CuisineType.INDIAN, CuisineType.MOROCCAN, CuisineType.TURKISH, CuisineType.AMERICAN);
            default:
                return Arrays.asList(CuisineType.values());
        }
    }

}