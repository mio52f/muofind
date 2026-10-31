package com.muo.find.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoEngine {

    private const val AES_GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12

    fun generateMasterKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256, SecureRandom())
        return keyGen.generateKey()
    }

    fun generateEphemeralId(masterKey: SecretKey, timeEpochMinutes: Long): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val buffer = ByteArray(16)
        for (i in 0..7) {
            buffer[i] = (timeEpochMinutes shr (i * 8)).toByte()
        }
        val encrypted = cipher.doFinal(buffer)
        return encrypted.copyOfRange(0, 10)
    }

    fun encryptLocationReport(payload: ByteArray, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(AES_GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val cipherText = cipher.doFinal(payload)
        return Pair(iv, cipherText)
    }
}
