package com.example.kooking.model;

import com.example.kooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CuisineType> preferredCuisine;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<MealType> preferredMealTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<DietType> dietaryRestrictions;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CookingMethod> preferredCookingMethods;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel preferredDifficulty;

    // The associated user
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}