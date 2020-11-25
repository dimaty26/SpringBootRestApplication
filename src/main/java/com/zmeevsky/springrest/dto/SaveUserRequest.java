package com.zmeevsky.springrest.dto;

import com.zmeevsky.springrest.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveUserRequest {

    private int id;

    @NotNull
    @NotBlank(message = "First Name is mandatory")
    @Size(min = 2, max = 20, message = "Should contain from {min} to {max} characters.")
    private String firstName;

    @NotNull
    @NotBlank(message = "Last Name is mandatory")
    @Size(min = 2, max = 20, message = "Should contain from {min} to {max} characters.")
    private String lastName;

    @NotNull
    @NotBlank(message = "Email is mandatory")
    @Size(min = 2, max = 20, message = "Should contain from {min} to {max} characters.")
    private String email;

    @NotNull
    @NotBlank(message = "Password is mandatory")
    private String password;

    private Set<Role> roles;
}
