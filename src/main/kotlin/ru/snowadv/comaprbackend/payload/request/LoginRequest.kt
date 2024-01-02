package ru.snowadv.comaprbackend.payload.request

import jakarta.validation.constraints.NotBlank

class LoginRequest(@NotBlank val username: String, @NotBlank var password: String)