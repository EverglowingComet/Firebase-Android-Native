package com.comet.freetester.core.remote.api

import android.util.Log
import com.comet.freetester.core.remote.callback.AsyncApiCallback
import com.comet.freetester.util.Utils
import com.google.firebase.functions.FirebaseFunctions

class MainApis {
    private val functions = FirebaseFunctions.getInstance()
    private val pTAG = "Main Api"

    fun invokeApiCall(apiName: String, params: HashMap<String, Any>, callback: AsyncApiCallback) {
        functions.getHttpsCallable(apiName).call(params).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val data = Utils.getDataMap(result)


                val success = Utils.checkSuccess(data)
                if (!success) {
                    Log.w(pTAG, "$apiName returned failure")
                    callback.onFailure(null)
                } else {
                    callback.onSuccess(data)
                }

            } else {
                task.exception?.let {
                    Log.w(pTAG, it)
                }
                callback.onFailure(task.exception?.message)
            }
        }
    }
}