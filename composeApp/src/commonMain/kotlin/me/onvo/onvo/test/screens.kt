package me.onvo.onvo.test


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Login Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Login") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLoginSuccess,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
    }
}

// Home Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUserClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(10) { index ->
                PostCard(
                    userId = "user_$index",
                    userName = "User $index",
                    content = "This is post content from user $index",
                    onUserClick = onUserClick
                )
            }
        }
    }
}

// Search Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onUserClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search users...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn {
                items(5) { index ->
                    UserListItem(
                        userId = "search_user_$index",
                        userName = "Search User $index",
                        onClick = onUserClick
                    )
                }
            }
        }
    }
}

// Discovery Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    onUserClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Discovery") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(8) { index ->
                PostCard(
                    userId = "discovery_user_$index",
                    userName = "Discovery User $index",
                    content = "Trending post content $index",
                    onUserClick = onUserClick
                )
            }
        }
    }
}

// Profile Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Current User Profile", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Email: user@example.com")
            Text("Posts: 42")
            Text("Followers: 1.2K")
        }
    }
}

// Settings Screen
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SettingsScreen(
//    onSettingClick: (String) -> Unit,
//    onLogout: () -> Unit
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Settings") })
//        }
//    ) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            items(listOf("Account", "Privacy", "Notifications", "Appearance", "Help")) { setting ->
//                SettingItem(
//                    title = setting,
//                    onClick = { onSettingClick(setting) }
//                )
//            }
//            item {
//                Divider(modifier = Modifier.padding(vertical = 8.dp))
//                SettingItem(
//                    title = "Logout",
//                    onClick = onLogout
//                )
//            }
//        }
//    }
//}

// User Profile Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("User ID: $userId", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Bio: This is a user bio")
            Text("Posts: 24")
            Text("Followers: 567")
        }
    }
}

// Settings Detail Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailScreen(
    settingType: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(settingType) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Settings for: $settingType")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Configure your $settingType settings here")
        }
    }
}

// Create Post Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPostCreated: () -> Unit,
    onBackClick: () -> Unit
) {
    var postContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                label = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPostCreated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post")
            }
        }
    }
}

// Helper Composables
@Composable
private fun PostCard(
    userId: String,
    userName: String,
    content: String,
    onUserClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { onUserClick(userId) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content)
        }
    }
}

@Composable
private fun UserListItem(
    userId: String,
    userName: String,
    onClick: (String) -> Unit
) {
    ListItem(
        headlineContent = { Text(userName) },
        modifier = Modifier.clickable { onClick(userId) }
    )
}

@Composable
private fun SettingItem(
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}