package com.comet.freetester.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.comet.freetester.R
import com.comet.freetester.databinding.ActivitySignUpBinding
import com.comet.freetester.model.DeliveryDataModel
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.ui.gallery.GalleryScreen
import com.comet.freetester.ui.template.ArenaActivity
import com.comet.freetester.util.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseError

class SignUpActivity : ArenaActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var agreed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

        val extraEmail = intent.getStringExtra(EXTRA_EMAIL)
        val extraPassword = intent.getStringExtra(EXTRA_PASSWORD)

        binding.email.setText(extraEmail)
        binding.password.setText(extraPassword)

        binding.btnSignUp.setOnClickListener {
            promptSignUp()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.termsService.setOnClickListener {
            Utils.showPrivacyPolicy(this, object : FirebaseDatabaseListener {
                override fun onSuccess() {
                    agreed = true
                    uiHandler.sendEmptyMessage(UIHandler.MSG_UPDATE_CONTENTS)
                }

                override fun onFailure(error: DatabaseError?) {
                    agreed = false
                    uiHandler.sendEmptyMessage(UIHandler.MSG_UPDATE_CONTENTS)
                }
            })
        }
    }

    override fun updateContents() {
        binding.termsService.isChecked = agreed
    }


    private fun promptSignUp() {
        if (!agreed) {
            Utils.showDialog(this, R.string.terms_service_prompt)
            return
        }
        val extraEmail = intent.getStringExtra(EXTRA_EMAIL)

        val userName: String = binding.username.getText().toString()
        val pass: String = binding.password.getText().toString()
        val emailStr: String = binding.email.getText().toString()
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(emailStr)) {
            uiHandler.sendEmptyMessage(UIHandler.MSG_INVALID_PARAM)
        } else {
            if (extraEmail == null || extraEmail != emailStr) {
                uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailStr.trim { it <= ' ' }, pass)
                    .addOnCompleteListener(
                        this
                    ) { task ->
                        if (task.exception == null) {
                            val uid = task.result.user!!.uid
                            dataModel = DeliveryDataModel.getInstance(uid)
                            dataModel.user.email = emailStr
                            dataModel.user.username = userName

                            dataModel.saveUser(object : FirebaseDatabaseListener {
                                override fun onSuccess() {
                                    loadInfo()
                                }

                                override fun onFailure(error: DatabaseError?) {
                                    uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                                    uiHandler.sendMessage(
                                        uiHandler.obtainMessage(
                                            UIHandler.MSG_SHOW_DIALOG,
                                            error?.message
                                        )
                                    )
                                }
                            })
                        } else {
                            uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                            val ex = task.exception
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                uiHandler.sendMessage(
                                    uiHandler.obtainMessage(
                                        UIHandler.MSG_SHOW_DIALOG,
                                        """
                            You have already signed up with this.
                            If you have account with this email on the older version, you can sign in with it and set user name, you can be part of new app.
                            """.trimIndent()
                                    )
                                )
                            } else {
                                uiHandler.sendMessage(
                                    uiHandler.obtainMessage(
                                        UIHandler.MSG_SHOW_DIALOG,
                                        task.exception!!.message
                                    )
                                )
                            }
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
            } else if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                val uid: String? = FirebaseAuth.getInstance().currentUser?.uid

                uiHandler.sendEmptyMessage(UIHandler.MSG_SHOW_PROGRESS)
                dataModel = DeliveryDataModel.getInstance(uid)
                dataModel.user.email = emailStr
                dataModel.user.username = userName

                dataModel.saveUser(object : FirebaseDatabaseListener {
                    override fun onSuccess() {
                        loadInfo()
                    }

                    override fun onFailure(error: DatabaseError?) {
                        uiHandler.sendEmptyMessage(UIHandler.MSG_HIDE_PROGRESS)
                        uiHandler.sendMessage(
                            uiHandler.obtainMessage(
                                UIHandler.MSG_SHOW_DIALOG,
                                error?.message
                            )
                        )
                    }
                })
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

    private fun openMain() {
        startActivity(Intent(this, GalleryScreen::class.java))
    }

    override fun handleMessage(message: Int, `object`: Any?) {
        when (message) {
            UIHandler.MSG_SHOW_MAIN -> {
                openMain()
            }
        }
    }

    companion object {
        val EXTRA_UID = "uid"
        val EXTRA_FACEBOOK_LOGIN = "facebook.login"
        val EXTRA_TYPE = "type"
        val EXTRA_EMAIL = "email"
        val EXTRA_PASSWORD = "password"
    }
}