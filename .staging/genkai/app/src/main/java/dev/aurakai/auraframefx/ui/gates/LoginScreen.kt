package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Login Screen
 *
 * Authentication screen with:
 * - Email/password input
 * - Biometric authentication option
 * - Remember me toggle
 * - Password reset link
 * - New user registration link
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    returnDestination: String? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Login form state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // UI state
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            if (returnDestination != null) {
                TopAppBar(
                    title = { Text("Login Required") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A1A),
                        titleContentColor = Color.Cyan
                    )
                )
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF001a33),
                                Color.Black,
                                Color(0xFF003366)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/Title
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Cyan,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AuraKai Login",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )

                if (returnDestination != null) {
                    Text(
                        text = "Login required to access protected features",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Error message
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.Red
                            )
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Cyan,
                        focusedLabelColor = Color.Cyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.Cyan,
                        focusedLeadingIconColor = Color.Cyan,
                        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.5f)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                }
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Cyan,
                        focusedLabelColor = Color.Cyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.Cyan,
                        focusedLeadingIconColor = Color.Cyan,
                        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.5f),
                        focusedTrailingIconColor = Color.Cyan,
                        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.5f)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Remember me & Forgot password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Cyan,
                                uncheckedColor = Color.White.copy(alpha = 0.5f)
                            ),
                            enabled = !isLoading
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Cyan
                        ),
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            // TODO: Navigate to password reset
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login button
                Button(
                    onClick = {
                        scope.launch {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter email and password"
                                return@launch
                            }

                            isLoading = true
                            errorMessage = null

                            // TODO: Implement actual authentication
                            delay(1500) // Simulate API call

                            // Mock authentication
                            if (email.isNotBlank() && password.length >= 4) {
                                // Success
                                if (returnDestination != null) {
                                    navController.navigate(returnDestination) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            } else {
                                errorMessage = "Invalid email or password"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Biometric login button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            // TODO: Implement biometric authentication
                            delay(1000)
                            isLoading = false
                            if (returnDestination != null) {
                                navController.navigate(returnDestination) {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Cyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Login with Biometrics",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Register link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Cyan,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            // TODO: Navigate to registration
                        }
                    )
                }
            }
        }
    }
}
