package com.example.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity(name="users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JsonIgnore
    private String name;

    @NotNull
    private String email;

    @NotNull
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String phoneNumber;
}
