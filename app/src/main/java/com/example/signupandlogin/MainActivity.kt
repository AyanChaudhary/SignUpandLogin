package com.example.signupandlogin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.signupandlogin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()
        val sharedPref=getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
        val editor=sharedPref.edit()
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            editor.putString("isLoggedIn","no").apply()
            Intent(this,SignInActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        editor.apply{
            putString("isLoggedIn","yes")
            apply()
        }
    }

}