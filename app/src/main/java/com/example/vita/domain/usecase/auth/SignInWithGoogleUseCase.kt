package com.example.vita.domain.usecase.auth

import android.content.Context
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(context: Context): Result<User> {
        return repository.signInWithGoogle(context)
    }
}
