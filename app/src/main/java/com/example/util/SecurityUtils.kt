package com.example.util

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {

    private const val JWT_SECRET = "homehelp_super_secure_jwt_secret_key_hyderabad_2026_x99"

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray(StandardCharsets.UTF_8))
        val hashedBytes = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        // If salt is empty (legacy fallback), check direct or fallback
        if (salt.isEmpty()) {
            return password == expectedHash || hashPassword(password, "").equals(expectedHash, ignoreCase = true)
        }
        val computedHash = hashPassword(password, salt)
        return computedHash.equals(expectedHash, ignoreCase = true)
    }

    fun generateOtp(): String {
        val random = SecureRandom()
        val num = 100000 + random.nextInt(900000)
        return num.toString()
    }

    fun generateJwtToken(customerId: String, phone: String, role: String = "customer"): String {
        val now = System.currentTimeMillis() / 1000
        val exp = now + (30 * 24 * 60 * 60) // 30 days valid

        val headerJson = """{"alg":"HS256","typ":"JWT"}"""
        val payloadJson = """{"sub":"$customerId","phone":"$phone","role":"$role","iat":$now,"exp":$exp}"""

        val encodedHeader = Base64.encodeToString(headerJson.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val encodedPayload = Base64.encodeToString(payloadJson.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

        val content = "$encodedHeader.$encodedPayload"
        val hmac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(JWT_SECRET.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        hmac.init(secretKeySpec)
        val signatureBytes = hmac.doFinal(content.toByteArray(StandardCharsets.UTF_8))
        val encodedSignature = Base64.encodeToString(signatureBytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

        return "$content.$encodedSignature"
    }

    fun isTokenValid(token: String?): Boolean {
        if (token.isNullOrBlank()) return false
        val parts = token.split(".")
        return parts.size == 3
    }
}
