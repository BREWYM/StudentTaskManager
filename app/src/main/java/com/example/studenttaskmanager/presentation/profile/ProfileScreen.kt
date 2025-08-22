package com.example.studenttaskmanager.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    navController: NavController,
) {
    // Загрузка данных при старте экрана
    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    val currentUser = viewModel.currentUser
    val members = viewModel.members
    val name by remember { derivedStateOf { viewModel.name } }
    val group by remember { derivedStateOf { viewModel.currentGroupName } }
    val message by remember { derivedStateOf { viewModel.message } }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Имя: $name")

        group?.let {
            Text("Группа: $it")
            Spacer(Modifier.height(8.dp))
            if (currentUser?.groupId != null) {
                GroupMembersSection(
                    members = members,
                    currentUser = currentUser,
                    adminId = viewModel.adminId.orEmpty(),
                ) { viewModel.removeMember(it) }
            }
            InviteCodeSection(viewModel.inviteCode)
            Button( modifier = Modifier
                .align(Alignment.End),
                onClick = { viewModel.leaveGroup() }
            ) {
                Text("Выйти из группы", fontSize = 12.sp)
            }
        } ?: run {
            OutlinedTextField(
                value = viewModel.groupName,
                onValueChange = viewModel::onGroupNameChange,
                label = { Text("Имя новой группы") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.createGroup() },
                enabled = viewModel.groupName.isNotBlank()
            ) {
                Text("Создать группу")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.inviteCode,
                onValueChange = viewModel::onInviteCodeChange,
                label = { Text("Код группы") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.joinGroup() },
                enabled = viewModel.inviteCode.isNotBlank()
            ) {
                Text("Присоединиться")
            }

            message?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = Color.Green)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                navController.navigate("tasks") {
                    popUpTo("tasks") { inclusive = false } // очищаем стек до задач
                }
            },
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Настройки", modifier = Modifier.padding(end = 8.dp))
            Text("Настроить напоминания", color = Color.White, fontSize = 16.sp)

        }
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("tasks") { inclusive = true } // очищаем стек до логина
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("ВЫЙТИ", color = Color.White, fontSize = 16.sp)

        }
    }
}

@Composable
fun InviteCodeSection(inviteCode: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Button(
            onClick = {
                if (isVisible) {
                    clipboardManager.setText(AnnotatedString(inviteCode))
                    Toast.makeText(context, "Код скопирован", Toast.LENGTH_SHORT).show()
                } else {
                    isVisible = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isVisible) "Скопировать код" else "Показать код")
        }

        if (isVisible) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Код: $inviteCode",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
