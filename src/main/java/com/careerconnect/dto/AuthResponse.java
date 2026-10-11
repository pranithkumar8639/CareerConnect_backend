
package com.careerconnect.dto;

import com.careerconnect.user.Role;

public record AuthResponse(
        String token,
        Long id,
        String fullName,
        String email,
        Role role
) {
}
