package com.kazanion.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseException
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthManager private constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null

        fun getInstance(): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager().also { INSTANCE = it }
            }
        }
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    val isPhoneVerified: Boolean
        get() = auth.currentUser?.phoneNumber != null

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser {
        return try {
            auth.signInWithEmailAndPassword(email, password).await().user
                ?: throw Exception("Authentication failed")
        } catch (e: Exception) {
            throw Exception("Authentication failed: ${e.message}")
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await().user
                ?: throw Exception("User creation failed")
        } catch (e: Exception) {
            throw Exception("User creation failed: ${e.message}")
        }
    }

    suspend fun sendEmailVerification() {
        try {
            auth.currentUser?.sendEmailVerification()?.await()
        } catch (e: Exception) {
            throw Exception("Failed to send verification email: ${e.message}")
        }
    }

    suspend fun sendPhoneVerification(phoneNumber: String, activity: android.app.Activity): String {
        return suspendCoroutine { continuation ->
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    continuation.resume(credential.smsCode ?: "")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    continuation.resumeWithException(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    continuation.resume(verificationId)
                }
            }

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                java.util.concurrent.TimeUnit.SECONDS,
                activity,
                callbacks
            )
        }
    }

    suspend fun verifyPhoneNumber(verificationId: String, code: String): FirebaseUser {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        return try {
            auth.currentUser?.linkWithCredential(credential)?.await()?.user
                ?: throw Exception("Phone verification failed")
        } catch (e: Exception) {
            throw Exception("Phone verification failed: ${e.message}")
        }
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await().user
                ?: throw Exception("Google sign in failed")
        } catch (e: Exception) {
            throw Exception("Google sign in failed: ${e.message}")
        }
    }

    fun signOut() {
        auth.signOut()
    }
} 