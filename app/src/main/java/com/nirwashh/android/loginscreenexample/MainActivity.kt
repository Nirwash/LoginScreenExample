package com.nirwashh.android.loginscreenexample

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nirwashh.android.loginscreenexample.databinding.ActivityMainBinding

const val TAG = "MainActivity"
const val KEY = "screenState"
class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private companion object {
        const val INITIAL = 0
        const val PROGRESS = 1
        const val SUCCESS = 2
        const val FAILED = 3
    }

    private var state = INITIAL

    private val textWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(p0: Editable?) {
//            Log.d(TAG, "changed: $p0")
            b.textInputLayout.isErrorEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        Log.d(TAG, "onCreate: savedInstanceState - ${savedInstanceState == null}")
        savedInstanceState?.let {
            state = it.getInt(KEY)
        }
        Log.d(TAG, "state is $state")

        when (state) {
            FAILED -> showDialog(b.contentLayout)
            SUCCESS -> {
                Snackbar.make(b.contentLayout, "Success", Snackbar.LENGTH_LONG).show()
                state = INITIAL
            }
        }


        spannableText()

        b.textInputPassword.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(p0: Editable?) {
                b.textInputLayout2.isErrorEnabled = false
            }
        })

        b.checkbox.setOnCheckedChangeListener { _, isChecked ->
            b.btnLogin.isEnabled = isChecked
        }

        b.btnLogin.setOnClickListener {
            val validEmail =
                android.util.Patterns.EMAIL_ADDRESS.matcher(b.textInputEmail.text.toString())
                    .matches()
            b.textInputLayout.isErrorEnabled = !validEmail
            val errorEmail = if (validEmail) "" else getString(R.string.invalid_email_message)
            b.textInputLayout.error = errorEmail
            val validPassword = !b.textInputPassword.text.isNullOrEmpty()
            b.textInputLayout2.isErrorEnabled = !validPassword
            val errorPassword = if (validPassword) "" else getString(R.string.invalid_password)
            b.textInputLayout2.error = errorPassword
            hideKeyboard(this@MainActivity, b.textInputPassword)
            if (validEmail && validPassword) {
                b.contentLayout.visibility = View.GONE
                b.progressBar.visibility = View.VISIBLE
                state = PROGRESS
                Handler(Looper.myLooper()!!).postDelayed({
                    state = FAILED
                    b.contentLayout.visibility = View.VISIBLE
                    b.progressBar.visibility = View.GONE
                    showDialog(b.contentLayout)
                }, 3000)
            }

        }


    }

    override fun onResume() {
        super.onResume()
        b.textInputEmail.addTextChangedListener(textWatcher)
    }

    override fun onPause() {
        super.onPause()
        b.textInputEmail.removeTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY, state)
    }

//region methods

    private fun showDialog(viewGroup: ViewGroup) {
        val myDialogFragment = MyDialogFragment()
        myDialogFragment.show(supportFragmentManager, "MyDialog")
        if (!myDialogFragment.isVisible) state = INITIAL
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm =
            context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun spannableText() {
        val fullText = getString(R.string.agreement_full_text)
        val use = getString(R.string.use)
        val startIndexOfUse = fullText.indexOf(use)
        val endIndexOfUse = startIndexOfUse + use.length
        val policy = getString(R.string.policy)
        val startIndexOfPolicy = fullText.indexOf(policy)
        val endIndexOfPolicy = startIndexOfPolicy + policy.length
        val spannableString = SpannableString(fullText)
        val useClickable = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#FF0000")
            }

            override fun onClick(p0: View) {
                Toast.makeText(this@MainActivity, "USE", Toast.LENGTH_SHORT).show()
            }
        }
        val policyClickable = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#FF0000")
            }

            override fun onClick(p0: View) {
                Toast.makeText(this@MainActivity, "POLICY", Toast.LENGTH_SHORT).show()
            }
        }
        spannableString.setSpan(useClickable, startIndexOfUse, endIndexOfUse, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(policyClickable, startIndexOfPolicy, endIndexOfPolicy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.tvAgreement.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
//endregion

}