package com.fooddeliveryapp.foodexpress.dto;

import com.fooddeliveryapp.foodexpress.entity.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
