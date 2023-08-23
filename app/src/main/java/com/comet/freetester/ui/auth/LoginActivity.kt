package com.comet.freetester.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.comet.freetester.R
import com.comet.freetester.databinding.ActivityLoginBinding
import com.comet.freetester.model.DeliveryDataModel
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.ui.gallery.GalleryScreen
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.util.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError

class LoginActivity : ArenaActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

        binding.btnLogin.setOnClickListener {
            promptLogin()
        }

        binding.btnSignup.setOnClickListener {
            promptSignUp()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.forgotPwd.setOnClickListener {
            onForgotPressed()
        }
    }

    private fun promptLogin() {
        val emailStr: String = binding.email.text.toString()
        val pass: String = binding.password.text.toString()

        if (TextUtils.isEmpty(emailStr) || TextUtils.isEmpty(pass)) {
            uiHandler.sendMessage(
                uiHandler.obtainMessage(
                    UIHandler.MSG_SHOW_DIALOG,
                    getString(R.string.login_email_password_error)
                )
            )
        } else {
            uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailStr.trim { it <= ' ' }, pass)
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.exception == null) {
                        dataModel = DeliveryDataModel.getInstance(task.result.user!!.uid)
                        loadInfo()
                    } else {
                        uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                        uiHandler.sendMessage(
                            uiHandler.obtainMessage(
                                UIHandler.MSG_SHOW_DIALOG,
                                task.exception?.message
                            )
                        )
                    }
                }.addOnFailureListener(this) { e ->
                    uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                    uiHandler.sendMessage(
                        uiHandler.obtainMessage(
                            UIHandler.MSG_SHOW_DIALOG,
                            e.message
                        )
                    )
                }
        }
    }

    private fun loadInfo() {
        dataModel.loadUser(object : FirebaseDatabaseListener{
            override fun onSuccess() {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                uiHandler.sendEmptyMessageDelayed(UIHandler.MSG_SHOW_MAIN, 100)
            }

            override fun onFailure(error: DatabaseError?) {
                uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_NET_ERROR)
            }
        })
    }

    private fun promptSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)

        intent.putExtra(SignUpActivity.EXTRA_EMAIL, binding.email.text.toString())
        intent.putExtra(SignUpActivity.EXTRA_PASSWORD, binding.password.text.toString())
        startActivity(intent)
    }

    private fun onForgotPressed() {
        Utils.showEditBoxDialog(this, R.string.forgot_password_prompt, R.string.email_placeholder, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) { update ->
            if (TextUtils.isEmpty(update)) {
                Utils.showDialog(this, R.string.invalid_values_prompt)
                return@showEditBoxDialog
            }
            uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
            FirebaseAuth.getInstance().sendPasswordResetEmail(update)
                .addOnCompleteListener {
                    uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                    uiHandler.sendMessage(
                        uiHandler.obtainMessage(
                            UIHandler.MSG_SHOW_DIALOG,
                            getString(R.string.forgot_password_success)
                        )
                    )
                }
                .addOnFailureListener {
                    uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                    uiHandler.sendMessage(
                        uiHandler.obtainMessage(
                            UIHandler.MSG_SHOW_DIALOG,
                            getString(R.string.connect_failed)
                        )
                    )
                }
        }
    }

    private val actionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            openMain()
        }
    }

    private fun openMain() {
        dataModel = DeliveryDataModel.getInstance()
        startActivity(Intent(this, GalleryScreen::class.java))
        finish()
    }

    override fun handleMessage(message: Int, `object`: Any?) {
        when (message) {
            UIHandler.MSG_SHOW_MAIN -> {
                openMain()
            }
        }
    }
}