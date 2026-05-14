package com.example.core

import io.ktor.server.auth.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.features.users.ExpertProfiles.userId
import java.util.Date

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET")
        ?: throw IllegalArgumentException("Missing secret environment variable")
    private const val issuer = "skipper-backend"
    private const val validityInMs = 3600000 * 24

    private val algorithm = Algorithm.HMAC512(secret)

    fun generateToken(userId: String, role: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()
}
