package com.example.studenttaskmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studenttaskmanager.presentation.add_task.AddTaskScreen
import com.example.studenttaskmanager.presentation.login.LoginScreen
import com.example.studenttaskmanager.presentation.login.SignUpScreen
import com.example.studenttaskmanager.presentation.profile.ProfileScreen
import com.example.studenttaskmanager.presentation.task_details.TaskDetailsScreen
import com.example.studenttaskmanager.presentation.task_list.TaskListScreen
import com.example.studenttaskmanager.ui.theme.StudentTaskManagerTheme
import com.google.firebase.auth.FirebaseAuth
import android.Manifest

class MainActivity : ComponentActivity() {
    //    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        createNotificationChannel(this)
        setContent {
            StudentTaskManagerTheme {
                val navController = rememberNavController()
                val auth = remember { FirebaseAuth.getInstance() }
                NavHost(
                    navController,
                    startDestination =
                        if (auth.currentUser != null) "tasks" else "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("tasks") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            { navController.navigate("signup") }
                        )
                    }
                    composable("signup") {
                        SignUpScreen(
                            onSignUpSuccess = {
                                navController.navigate("tasks") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            },
                            { navController.navigate("login") }
                        )
                    }
                    composable("tasks") {
                        TaskListScreen(navController = navController)
                    }
                    composable("add_task") {
                        AddTaskScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("profile"){
                        ProfileScreen(
                            navController = navController
                        )
                    }
                    composable("taskDetails/{taskId}/{subjectName}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                        val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
                        TaskDetailsScreen(
                            taskId = taskId,
                            navController = navController,
                            subjectNameFromArgs = subjectName
                        )
                    }
                }
            }
        }

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))  // из google-services.json
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "deadline_channel",
        "Напоминания о дедлайнах",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Уведомления о приближающихся дедлайнах"
    }

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}
