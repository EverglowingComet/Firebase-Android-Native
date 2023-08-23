package com.comet.freetester.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.comet.freetester.R
import com.comet.freetester.model.DeliveryDataModel
import com.comet.freetester.model.FirebaseDatabaseListener
import com.comet.freetester.ui.gallery.GalleryScreen
import com.comet.freetester.ui.template.ArenaActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError

class SplashActivity : ArenaActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initViews()

        val intent = this.intent
        if (intent.hasCategory(Intent.CATEGORY_BROWSABLE)) {
            // intent came from browser
            val data: Uri? = intent.data
            val mode: String? = data?.getQueryParameter("mode")
            val oobCode: String? = data?.getQueryParameter("oobCode")
            if (mode !== "verifyEmail" || oobCode == null) {
                Log.i("MyActivity", "Started with unexpected browsable intent")
                return
            }
            handleVerifyEmailActionCode(oobCode)
        } else {
            promptOpen()
        }
    }

    private fun promptOpen() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            dataModel = DeliveryDataModel.getInstance(uid)
            loadInfo()
        } else {
            uiHandler.sendEmptyMessageDelayed(UIHandler.MSG_SHOW_NEXT, 2000L)
        }
    }

    private fun handleVerifyEmailActionCode(oobCode: String) {
        var email: String? = null
        FirebaseAuth.getInstance().checkActionCode(oobCode)
            .onSuccessTask { actionCodeResult: ActionCodeResult ->
                if (actionCodeResult.operation != ActionCodeResult.VERIFY_EMAIL) {
                    throw UnsupportedActionCodeOperationException(actionCodeResult.operation)
                }
                email = actionCodeResult.info!!.email
                FirebaseAuth.getInstance().applyActionCode(oobCode)
            }
            .addOnCompleteListener { applyActionCodeTask: Task<Void> ->
                if (!applyActionCodeTask.isSuccessful) {
                    val ex = applyActionCodeTask.exception
                    // TODO: Handle exceptions
                    Toast.makeText(
                        applicationContext,
                        "Failed to verify email. " + ex!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnCompleteListener
                }

                // email verified successfully!
                // do something interesting
                Toast.makeText(applicationContext, "$email was verified!", Toast.LENGTH_SHORT)
                    .show()
                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser// WARN: May not have finished initializing yet?
                if (user != null && user.email === email) {
                    user.reload()
                        .addOnCompleteListener {
                            promptOpen()
                        }
                        .addOnFailureListener { ex: Exception ->
                            promptOpen()
                        }
                } else {
                    promptOpen()
                }
            }
    }

    override fun handleMessage(message: Int, `object`: Any?) {
        when (message) {
            UIHandler.MSG_SHOW_NEXT -> {
                //startActivity(Intent(this, SetupActivity::class.java))
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            UIHandler.MSG_SHOW_MAIN -> {
                openMain()
            }
        }
    }

    private fun loadInfo() {
        dataModel.loadUser(object : FirebaseDatabaseListener {
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
        finish()
    }
}
class UnsupportedActionCodeOperationException(var operation: Int) :
    java.lang.Exception("The ActionCodeResult.Operation value of $operation is not supported")