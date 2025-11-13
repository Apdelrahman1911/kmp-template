// File: commonMain/kotlin/me/onvo/onvo/presentation/ui/PasswordResetScreen.kt
package me.onvo.onvo.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.onvo.onvo.presentation.viewmodel.PasswordResetStep
import me.onvo.onvo.presentation.viewmodel.PasswordResetViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(
    viewModel: PasswordResetViewModel = koinInject(),
    onBackClick: () -> Unit,
    onPasswordChanged: (isFromSettings: Boolean) -> Unit,
    isFromSettings: Boolean = false,
    prefilledUserId: String? = null
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Initialize from settings if applicable
    LaunchedEffect(prefilledUserId) {
        if (isFromSettings && !prefilledUserId.isNullOrEmpty()) {
            viewModel.initializeFromSettings(prefilledUserId)
        }
    }

    // Handle success - pass isFromSettings flag to callback
    LaunchedEffect(state.currentStep) {
        if (state.currentStep is PasswordResetStep.Success) {
            val successStep = state.currentStep as PasswordResetStep.Success
            onPasswordChanged(successStep.isFromSettings)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isFromSettings) "Change Password" else "Reset Password",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Step indicator (only if not from settings)
                if (!isFromSettings) {
                    EnhancedStepIndicator(currentStep = state.currentStep)
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Main Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Show loading overlay
                        if (state.isLoading) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Processing...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            // Show appropriate step content
                            AnimatedContent(
                                targetState = state.currentStep,
                                transitionSpec = {
                                    fadeIn() + slideInVertically() togetherWith
                                            fadeOut() + slideOutVertically()
                                }
                            ) { step ->
                                when (step) {
                                    is PasswordResetStep.EnterUserId -> {
                                        UserInputStep(
                                            viewModel = viewModel,
                                            error = state.error
                                        )
                                    }
                                    is PasswordResetStep.EnterCode -> {
                                        CodeStep(
                                            viewModel = viewModel,
                                            message = step.message,
                                            error = state.error
                                        )
                                    }
                                    is PasswordResetStep.EnterNewPassword -> {
                                        PasswordStep(
                                            viewModel = viewModel,
                                            error = state.error
                                        )
                                    }
                                    is PasswordResetStep.Success -> {
                                        SuccessStep(
                                            userName = step.userName,
                                            isFromSettings = step.isFromSettings
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun EnhancedStepIndicator(currentStep: PasswordResetStep) {
    val stepNumber = when (currentStep) {
        is PasswordResetStep.EnterUserId -> 1
        is PasswordResetStep.EnterCode -> 2
        is PasswordResetStep.EnterNewPassword -> 3
        is PasswordResetStep.Success -> 3
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnhancedStepCircle(
            number = 1,
            label = "Verify",
            icon = Icons.Default.Person,
            isActive = stepNumber >= 1,
            isComplete = stepNumber > 1
        )
        AnimatedStepLine(isActive = stepNumber > 1)
        EnhancedStepCircle(
            number = 2,
            label = "Code",
            icon = Icons.Default.Email,
            isActive = stepNumber >= 2,
            isComplete = stepNumber > 2
        )
        AnimatedStepLine(isActive = stepNumber > 2)
        EnhancedStepCircle(
            number = 3,
            label = "Password",
            icon = Icons.Default.Lock,
            isActive = stepNumber >= 3,
            isComplete = false
        )
    }
}

@Composable
private fun EnhancedStepCircle(
    number: Int,
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    isComplete: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = when {
                isComplete -> MaterialTheme.colorScheme.primary
                isActive -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            shadowElevation = if (isActive) 4.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isComplete) Icons.Default.Check else icon,
                    contentDescription = null,
                    tint = when {
                        isComplete -> MaterialTheme.colorScheme.onPrimary
                        isActive -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AnimatedStepLine(isActive: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(500)
    )

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun SuccessStep(userName: String, isFromSettings: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = "Success!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isFromSettings) {
                "Your password has been changed successfully"
            } else {
                "Welcome back, $userName!\nRedirecting to home..."
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserInputStep(
    viewModel: PasswordResetViewModel,
    error: String?
) {
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            icon = Icons.Default.Person,
            title = "Enter Your Information",
            subtitle = "We'll send a reset code to your registered email"
        )

        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it
                viewModel.clearError()
            },
            label = { Text("Username, Email, or Phone") },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (userInput.isNotEmpty()) viewModel.requestCodeByInput(userInput) }
            ),
            isError = error != null
        )

        if (error != null) {
            ErrorMessage(error)
        }

        Button(
            onClick = { viewModel.requestCodeByInput(userInput) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = userInput.isNotEmpty(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Send, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Send Reset Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CodeStep(
    viewModel: PasswordResetViewModel,
    message: String?,
    error: String?
) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            icon = Icons.Default.Email,
            title = "Enter Reset Code",
            subtitle = message ?: "Enter the 6-digit code sent to your email"
        )

        OutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    code = it
                    viewModel.clearError()
                }
            },
            label = { Text("6-Digit Code") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (code.length == 6) viewModel.submitCode(code) }
            ),
            isError = error != null
        )

        if (error != null) {
            ErrorMessage(error)
        }

        Button(
            onClick = { viewModel.submitCode(code) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = code.length == 6,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.VerifiedUser, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Verify Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        TextButton(
            onClick = { viewModel.resendCode() }
        ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Resend Code")
        }
    }
}

@Composable
private fun PasswordStep(
    viewModel: PasswordResetViewModel,
    error: String?
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            icon = Icons.Default.Key,
            title = "Create New Password",
            subtitle = "Password must be at least 6 characters"
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                viewModel.clearError()
            },
            label = { Text("New Password") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Hide" else "Show"
                    )
                }
            },
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                viewModel.clearError()
            },
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showConfirmPassword) "Hide" else "Show"
                    )
                }
            },
            visualTransformation = if (showConfirmPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty())
                        viewModel.changePassword(newPassword, confirmPassword)
                }
            ),
            isError = error != null
        )

        if (error != null) {
            ErrorMessage(error)
        }

        Button(
            onClick = { viewModel.changePassword(newPassword, confirmPassword) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = newPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Change Password",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StepHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}