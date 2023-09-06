package com.comet.freetester.core.remote.callback

interface AsyncApiCallback {
    fun onSuccess(dict: HashMap<String, Any?>?)
    fun onFailure(msg: String?)
}