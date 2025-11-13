// File: commonMain/kotlin/me/onvo/onvo/presentation/ui/ProfileScreen.kt
package me.onvo.onvo.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.onvo.onvo.domain.model.*
import me.onvo.onvo.presentation.viewmodel.ProfileViewModel
import org.koin.compose.koinInject
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: ProfileViewModel = koinInject()
) {
    val isOwnProfile = userId == null
    val scrollState = rememberLazyListState()

    // Load profile data
    LaunchedEffect(userId) {
        if (isOwnProfile) {
            viewModel.loadCurrentUserStatus()
        } else {
            viewModel.loadUserProfile(userId)
        }
    }

    val currentUserState by viewModel.currentUserState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // Load full profile when auth status is ready
    LaunchedEffect(currentUserState) {
        if (isOwnProfile && currentUserState is AuthStatusUiState.Success) {
            val userData = (currentUserState as AuthStatusUiState.Success).user
            viewModel.loadUserProfile(userData.id)
        }
    }

    // Calculate scroll progress for animations
    val scrollOffset = remember {
        derivedStateOf {
            val firstVisibleItem = scrollState.firstVisibleItemIndex
            val firstVisibleOffset = scrollState.firstVisibleItemScrollOffset

            if (firstVisibleItem == 0) {
                min(firstVisibleOffset / 300f, 1f)
            } else {
                1f
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = profileState) {
            is ProfileUiState.Loading -> LoadingScreen()
            is ProfileUiState.Success -> {
                ProfileContent(
                    profile = state.profile,
                    isOwnProfile = isOwnProfile,
                    scrollState = scrollState,
                    scrollProgress = scrollOffset.value,
                    onRefresh = {
                        if (isOwnProfile) {
                            (currentUserState as? AuthStatusUiState.Success)?.user?.id?.let {
                                viewModel.refreshProfile(it)
                            }
                        } else {
                            userId?.let { viewModel.refreshProfile(it) }
                        }
                    }
                )
            }
            is ProfileUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = {
                        if (isOwnProfile) {
                            viewModel.loadCurrentUserStatus()
                        } else {
                            userId?.let { viewModel.retry(it) }
                        }
                    }
                )
            }
        }

        // Animated Top App Bar
        if (profileState is ProfileUiState.Success) {
            AnimatedTopAppBar(
                profile = (profileState as ProfileUiState.Success).profile,
                scrollProgress = scrollOffset.value,
                isOwnProfile = isOwnProfile,
                onBackClick = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedTopAppBar(
    profile: UserProfile,
    scrollProgress: Float,
    isOwnProfile: Boolean,
    onBackClick: (() -> Unit)?
) {
    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = scrollProgress > 0.7f,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = profile.image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = profile.fullname,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            if (!isOwnProfile) {
                IconButton(onClick = { onBackClick?.invoke() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = scrollProgress
            )
        )
    )
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    isOwnProfile: Boolean,
    scrollState: androidx.compose.foundation.lazy.LazyListState,
    scrollProgress: Float,
    onRefresh: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Threads", "Collections", "Posts")

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize()
    ) {
        // Cover & Profile Header Section
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Cover Image with Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = profile.image, // Use cover image if available
                        contentDescription = "Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                }

                // Profile Image - Positioned to overlap cover
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Profile Image
                    val imageSize by animateDpAsState(
                        targetValue = if (scrollProgress > 0.5f) 64.dp else 100.dp,
                        animationSpec = tween(durationMillis = 300)
                    )

                    val borderWidth by animateDpAsState(
                        targetValue = if (scrollProgress > 0.5f) 2.dp else 4.dp,
                        animationSpec = tween(durationMillis = 300)
                    )

                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .border(
                                borderWidth,
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = profile.image,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name with verification badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = profile.fullname,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF6B7FED),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Username
                    Text(
                        text = "@${profile.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )

                    // Bio
                    if (profile.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }

                    // Followers/Following
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${profile.cnt.followers} Followers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Text(
                            text = "${profile.cnt.following} Following",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Social Links Row
        item {
            SocialLinksRow(links = profile.lnks)
        }

        // Action Buttons
        item {
            ActionButtonsRow(
                isOwnProfile = isOwnProfile,
                followed = profile.followed,
                onPostClick = { /* TODO */ },
                onShareClick = { /* TODO */ },
                onFollowClick = { /* TODO */ }
            )
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                            )
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    )
                }
            }
        }

        // Content Area (Posts will be added later)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Posts will appear here",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SocialLinksRow(links: List<UserLink>) {
    // Filter and sort links
    val validLinks = links
        .filter {
            it.d.isNotBlank() &&
                    !it.d.trim().equals("Test", ignoreCase = true) &&
                    it.d.trim() != "Test "
        }
        .sortedBy { it.linkType.ordinal } // Sort by enum order
        .take(10)

    if (validLinks.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Social Media Title
            Text(
                text = "Connect",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                fontWeight = FontWeight.SemiBold
            )

            // Social Icons in scrollable row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                validLinks.forEach { link ->
                    SocialIconButton(link = link)
                }
            }
        }
    }
}

@Composable
private fun SocialIconButton(link: UserLink) {
    val (icon, color) = getSocialIconWithColor(link.linkType)

    Surface(
        onClick = { /* TODO: Open link */ },
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.1f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = link.linkType.displayName,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    isOwnProfile: Boolean,
    followed: Boolean,
    onPostClick: () -> Unit,
    onShareClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isOwnProfile) {
            // Post something button
            Button(
                onClick = onPostClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post something", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            // Share button
            OutlinedButton(
                onClick = onShareClick,
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            // Follow button
            Button(
                onClick = onFollowClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = if (followed) {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Text(
                    if (followed) "Following" else "Follow",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            // Share button
            OutlinedButton(
                onClick = onShareClick,
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                "Loading profile...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

private fun getSocialIconWithColor(linkType: LinkType): Pair<ImageVector, Color> {
    return when (linkType) {
        LinkType.LINKEDIN -> Icons.Default.Work to Color(0xFF0A66C2)
        LinkType.WHATSAPP -> Icons.Default.Phone to Color(0xFF25D366)
        LinkType.SPOTIFY -> Icons.Default.MusicNote to Color(0xFF1DB954)
        LinkType.SNAPCHAT -> Icons.Default.Camera to Color(0xFFFFFC00)
        LinkType.INSTAGRAM -> Icons.Default.CameraAlt to Color(0xFFE4405F)
        LinkType.REDDIT -> Icons.Default.Forum to Color(0xFFFF4500)
        LinkType.SOUND_CLOUD -> Icons.Default.Cloud to Color(0xFFFF5500)
        LinkType.PINTEREST -> Icons.Default.PushPin to Color(0xFFE60023)
        LinkType.GITHUB -> Icons.Default.Code to Color(0xFF181717)
        LinkType.TWITTER -> Icons.Default.Tag to Color(0xFF1DA1F2)
        LinkType.GMAIL -> Icons.Default.Email to Color(0xFFEA4335)
        LinkType.BEHANCE -> Icons.Default.Palette to Color(0xFF1769FF)
        LinkType.YOUTUBE -> Icons.Default.PlayArrow to Color(0xFFFF0000)
        LinkType.TIKTOK -> Icons.Default.MusicNote to Color(0xFF000000)
        LinkType.ANIME -> Icons.Default.Theaters to Color(0xFFFF6B9D)
        LinkType.FACEBOOK -> Icons.Default.ThumbUp to Color(0xFF1877F2)
        LinkType.OTHER -> Icons.Default.Link to Color(0xFF6B7280)
    }
}