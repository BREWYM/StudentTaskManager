package com.example.studenttaskmanager.data.remote

import android.util.Log
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
        val user = res.user ?: throw Exception("Пользователь не создан")

        // Сохраняем имя в FirebaseAuth профиле
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build()).await()
        // Сохраняем пользователя в Firestore
        saveUserToFirestore(user.uid, email, name)
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

    private suspend fun saveUserToFirestore(uid: String, email: String, name: String) {
        val userData = mapOf(
            "id" to uid,
            "email" to email,
            "name" to name,
            "groupId" to null
        )
        firestore.collection("users")
            .document(uid)
            .set(userData, SetOptions.merge())
            .await()
    }
}