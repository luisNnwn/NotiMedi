package com.example.notimedi

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

    @Composable
    fun rememberFirebaseAuthLauncher(
        onAuthComplete: (GoogleSignInAccount) -> Unit,
        onAuthError: () -> Unit
    ): ManagedActivityResultLauncher {
        val context = LocalContext.current
        val auth = FirebaseAuth.getInstance()

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { taskAuth ->
                        if (taskAuth.isSuccessful) {
                            onAuthComplete(account)
                        } else {
                            onAuthError()
                        }
                    }
            } catch (e: Exception) {
                onAuthError()
            }
        }

        return remember {
            ManagedActivityResultLauncher(
                launcher = launcher,
                createIntent = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signInIntent
                }
            )
        }
    }

    class ManagedActivityResultLauncher(
        private val launcher: ActivityResultLauncher<Intent>,
        private val createIntent: () -> Intent
    ) {
        fun launch() {
            launcher.launch(createIntent())
        }
    }

