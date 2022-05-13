package com.nirwashh.android.loginscreenexample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.nirwashh.android.loginscreenexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)


        b.textInputEmail.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(p0: Editable?) {
                b.textInputLayout.isErrorEnabled = false
                b.btnLogin.isEnabled = true
            }
        })

        b.textInputPassword.addTextChangedListener(object : SimpleTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                b.textInputLayout2.isErrorEnabled = false
                b.btnLogin.isEnabled = true
            }
        })

        b.btnLogin.setOnClickListener {
            val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(b.textInputEmail.text.toString()).matches()
            b.textInputLayout.isErrorEnabled = !validEmail
            val errorEmail = if (validEmail) "" else getString(R.string.invalid_email_message)
            b.textInputLayout.error = errorEmail
            val validPassword = !b.textInputPassword.text.isNullOrEmpty()
            b.textInputLayout2.isErrorEnabled = !validPassword
            val errorPassword = if (validPassword) "" else getString(R.string.invalid_password)
            b.textInputLayout2.error = errorPassword
            b.btnLogin.isEnabled = !validEmail && !validPassword
            hideKeyboard(this@MainActivity, b.textInputPassword)
        }





    }
    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}