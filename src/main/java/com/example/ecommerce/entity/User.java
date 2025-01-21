package com.example.ecommerce.entity;

import com.example.ecommerce.common.enums.user.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

//    @NotNull
    private UserRole role;
}
