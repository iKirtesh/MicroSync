package com.kirtesh.microsync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
}
