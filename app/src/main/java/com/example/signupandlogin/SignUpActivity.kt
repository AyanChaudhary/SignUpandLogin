package com.example.signupandlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.signupandlogin.databinding.ActivityMainBinding
import com.example.signupandlogin.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var emailId:String
    private lateinit var password:String
    private lateinit var confirmPassword:String
    private lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()
        init()
        binding.tvSignIn.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
        }
        binding.btnSignUp.setOnClickListener {
            init()
            if(emailId.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                && phoneNumber.isNotEmpty() && phoneNumber.length == 10){
                if( password ==  confirmPassword){
                    val user=auth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener{
                        if(it.isSuccessful){
                            val user=auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(phoneNumber).build())
                            user?.let {
                                onBackPressedDispatcher.onBackPressed()
                            }
                        }else{
                            Toast.makeText(this, it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Snackbar.make(binding.root,"Password doesn't match",Snackbar.LENGTH_SHORT).show()
                }

            }else{
                Snackbar.make(binding.root,"Please enter data in all fields",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(){
        emailId=binding.emailEt.text.toString()
        password=binding.passET.text.toString()
        confirmPassword=binding.confirmPassEt.text.toString()
        phoneNumber=binding.phoneET.text.toString()
    }
}