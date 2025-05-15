package com.example.studenttaskmanager.data.remote

import android.util.Log
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
): AuthRepository {
    override suspend fun login(email: String, password: String): Resource<FirebaseUser> = try {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        Log.e("Login", "Login with credentials")
        Resource.Success(res.user!!)
    } catch (e: Exception){
        Resource.Error(e.message?: "Возникла ошибка при входе")
    }

    override suspend fun register(email: String, password: String, name: String): Resource<FirebaseUser> = try {
        val res = auth.createUserWithEmailAndPassword(email, password).await()
        res.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
        Log.e("Register", "Регистрация")
        Resource.Success(auth.currentUser!!)
    } catch (e: Exception) {
        Resource.Error(e.message?: "Возникла ошибка при регистрации")
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<FirebaseUser> = try {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        val res = auth.signInWithCredential(cred).await()
        Log.e("Login", "Login with Google")
        Resource.Success(res.user!!)
    } catch (e: Exception) {
        Resource.Error(e.message?: "Возникла ошибка при входе c помощью аккаунта Google")
    }
    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}