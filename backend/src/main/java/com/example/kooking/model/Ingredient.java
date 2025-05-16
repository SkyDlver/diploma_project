package com.example.kooking.model;

import com.example.kooking.enums.IngredientCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@ToString(exclude = {"substitutes", "substituteFor"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private IngredientCategory category;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    private String nutritionalValue;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ingredient_substitutes",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "substitute_id")
    )
    private Set<Ingredient> substitutes;

    @ManyToMany(mappedBy = "substitutes", fetch = FetchType.LAZY)
    private Set<Ingredient> substituteFor;
}