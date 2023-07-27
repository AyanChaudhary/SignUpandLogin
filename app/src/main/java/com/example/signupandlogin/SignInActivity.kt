package com.example.signupandlogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.signupandlogin.databinding.ActivityMainBinding
import com.example.signupandlogin.databinding.ActivitySignInBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth : FirebaseAuth
    private var number: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()

        binding.tvSignUp.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            if(binding.emailEt.text.toString().isNotEmpty() &&
                binding.passET.text.toString().isNotEmpty()){
                auth.signInWithEmailAndPassword(binding.emailEt.text.toString(),
                    binding.passET.text.toString()).addOnCompleteListener { it ->
                    if(it.isSuccessful){
                            binding.progressBar.visibility= View.VISIBLE
                            number=auth.currentUser?.displayName
                            mobileVerification()
                        }else{
                            Snackbar.make(binding.root,"User doesn't exist. Please sign up", Snackbar.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun mobileVerification() {
        if(number?.length==10){
            number="+91$number"

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number!!) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Toast.makeText(this, "Authentication successful",Toast.LENGTH_SHORT).show()

                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, task.exception.toString(),Toast.LENGTH_SHORT).show()


                    }
                    binding.progressBar.visibility= View.INVISIBLE
                    // Update UI
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                binding.progressBar.visibility= View.INVISIBLE
                Toast.makeText(this@SignInActivity, e.toString(),Toast.LENGTH_SHORT).show()

                Log.d("TAG","onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                binding.progressBar.visibility= View.INVISIBLE
                Toast.makeText(this@SignInActivity, e.toString(),Toast.LENGTH_SHORT).show()

                Log.d("TAG","FirebaseTooManyRequestsException: ${e.toString()}")

            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                binding.progressBar.visibility= View.INVISIBLE
                Toast.makeText(this@SignInActivity, e.toString(),Toast.LENGTH_SHORT).show()


            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            val intent=Intent(this@SignInActivity,OtpActivity::class.java)
            intent.putExtra("OTP",verificationId)
            intent.putExtra("resendToken",token)
            binding.progressBar.visibility= View.INVISIBLE
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPref=getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val isLoggedIn=sharedPref.getString("isLoggedIn",null)
        if(isLoggedIn=="yes"){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}