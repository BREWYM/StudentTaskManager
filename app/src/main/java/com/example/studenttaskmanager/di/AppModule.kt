package com.example.studenttaskmanager.di

import com.example.studenttaskmanager.data.remote.AuthRepositoryImpl
import com.example.studenttaskmanager.data.remote.SubjectRepositoryImpl
import com.example.studenttaskmanager.data.remote.TaskRepositoryImpl
import com.example.studenttaskmanager.data.remote.UserRepositoryImpl
import com.example.studenttaskmanager.domain.repositories.AuthRepository
import com.example.studenttaskmanager.domain.repositories.SubjectRepository
import com.example.studenttaskmanager.domain.repositories.TaskRepository
import com.example.studenttaskmanager.domain.repositories.UserRepository
import com.example.studenttaskmanager.domain.use_cases.AddTaskUseCase
import com.example.studenttaskmanager.domain.use_cases.GetTasksUseCase
import com.example.studenttaskmanager.domain.use_cases.SignInUseCase
import com.example.studenttaskmanager.domain.use_cases.SignUpUseCase
import com.example.studenttaskmanager.presentation.add_task.AddTaskViewModel
import com.example.studenttaskmanager.presentation.login.AuthViewModel
import com.example.studenttaskmanager.presentation.profile.ProfileViewModel
import com.example.studenttaskmanager.presentation.task_details.TaskDetailsViewModel
import com.example.studenttaskmanager.presentation.task_list.TaskViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val firebaseModule = module {
    single<FirebaseAuth> { Firebase.auth }
    single<FirebaseFirestore> { Firebase.firestore }
//    single<FirebaseStorage> { Firebase.storage }
}

val repositoryModule = module {
    single<SubjectRepository> { SubjectRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(
        get<FirebaseAuth>(),
        firestore = get(),
    ) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    //single<GroupRepository> { GroupRepositoryImpl(get()) }
}
val useCaseModule = module {
    single { GetTasksUseCase(get()) }
    single { AddTaskUseCase(get()) }
    single{ SignInUseCase(get()) }
    single{ SignUpUseCase(get()) }
//    single { GetUserUseCase(get()) }
//    single { JoinGroupUseCase(get()) }
//    single { GetSubjectsUseCase(get()) }
}
val viewModelModule = module {
    viewModel { TaskViewModel(get()) }
    viewModel { AddTaskViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel{ ProfileViewModel(get(), get()) }
    viewModel{ TaskViewModel(get()) }
    viewModel{ TaskDetailsViewModel(get()) }
//    viewModel { GroupViewModel(get()) }
//    viewModel { AuthViewModel(get()) }
}
val appModule = listOf(
    repositoryModule,
    useCaseModule,
    viewModelModule,
    firebaseModule
)
