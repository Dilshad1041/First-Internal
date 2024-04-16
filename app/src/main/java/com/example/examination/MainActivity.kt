package com.example.examination
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examination.ui.theme.ExaminationTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExaminationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthenticationScreen()
                }
            }
        }
    }
}

@Composable
fun AuthenticationScreen() {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        // Phone number field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                    sendPhoneNumberToFirebase(context, phoneNumber)
                }
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )


        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                    sendOTPToFirebase(context, otp)
                }
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Button(
            onClick = {

                sendPhoneNumberToFirebase(context, phoneNumber)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Send Verification Code")
        }

        Button(
            onClick = {

                sendOTPToFirebase(context, otp)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Authenticate with OTP")
        }
    }
}

private var storedVerificationId: String? = null

private fun sendPhoneNumberToFirebase(context: Context, phoneNumber: String) {

    val formattedPhoneNumber = phoneNumber.replace("[^\\d]".toRegex(), "")

    if (!formattedPhoneNumber.startsWith("+")) {

        val countryCode = "+91"
        val formattedPhoneNumberWithCountryCode = "$countryCode$formattedPhoneNumber"


        sendPhoneNumberToFirebaseInternal(context, formattedPhoneNumberWithCountryCode)
    } else {

        sendPhoneNumberToFirebaseInternal(context, formattedPhoneNumber)
    }
}

private fun sendPhoneNumberToFirebaseInternal(context: Context, formattedPhoneNumber: String) {
    val auth = FirebaseAuth.getInstance()
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(formattedPhoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                signInWithPhoneAuthCredential(context, credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                storedVerificationId = verificationId
            }
        })
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}


private fun signInWithPhoneAuthCredential(context: Context, credential: PhoneAuthCredential) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun sendOTPToFirebase(context: Context, otp: String) {
    val auth = FirebaseAuth.getInstance()
    val verificationId = storedVerificationId

    if (verificationId != null) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
                    navigateToOTPScreen(context)
                } else {

                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    } else {

        Toast.makeText(context, "Verification ID not available", Toast.LENGTH_SHORT).show()
    }
}


private fun navigateToOTPScreen(context: Context) {
    val intent = Intent(context, Home::class.java)
    context.startActivity(intent)
}


@Preview(showBackground = true)
@Composable
fun AuthenticationScreenPreview() {
    ExaminationTheme {
        AuthenticationScreen()
    }
}