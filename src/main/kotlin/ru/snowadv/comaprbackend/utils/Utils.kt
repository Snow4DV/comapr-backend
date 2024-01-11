package ru.snowadv.comaprbackend.utils

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import ru.snowadv.comaprbackend.security.service.UserDetailsImpl

fun SecurityContext.currentUserDetailsOrNull(): UserDetailsImpl? {
    return try {
        val voterAuthToken = authentication as UsernamePasswordAuthenticationToken
        voterAuthToken.principal as UserDetailsImpl
    } catch (exception: ClassCastException) {
        null
    }
}