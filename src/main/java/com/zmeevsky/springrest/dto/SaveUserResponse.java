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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserResponse {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<Role> roles;
}
