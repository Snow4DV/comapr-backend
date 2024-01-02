package ru.snowadv.comaprbackend.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class SignupRequest(
    @NotBlank
    @Size(min = 3, max = 20) val username: String, @NotBlank
    @Size(max = 50)
    @Email val email: String, @NotBlank
    @Size(min = 6, max = 40) val password: String
)