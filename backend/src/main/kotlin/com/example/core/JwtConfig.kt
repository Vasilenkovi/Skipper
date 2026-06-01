package com.example.core

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
  private val secret =
    System.getenv("JWT_SECRET")
      //?: throw IllegalArgumentException("Missing secret environment variable")

      ?: "test-secret-key-for-local-runs"
  private const val ISSUER = "skipper-backend"
  private const val VALIDITY_IN_MS = 3600000 * 24

  private val algorithm = Algorithm.HMAC512(secret)

  fun generateToken(
    userId: String,
    role: String,
  ): String =
    JWT
      .create()
      .withIssuer(ISSUER)
      .withClaim("userId", userId)
      .withClaim("role", role)
      .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
      .sign(algorithm)

  val verifier: JWTVerifier =
    JWT
      .require(algorithm)
      .withIssuer(ISSUER)
      .build()
}
