package com.example.phonofit.firebase

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)