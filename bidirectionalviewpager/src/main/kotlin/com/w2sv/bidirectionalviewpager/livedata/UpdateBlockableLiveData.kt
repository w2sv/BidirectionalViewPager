package com.w2sv.bidirectionalviewpager.livedata

import androidx.lifecycle.LiveData
import com.w2sv.delegates.AutoSwitch

class UpdateBlockableLiveData<T>(value: T, private val convertValuePrePost: ((T) -> T) = { it }) :
    LiveData<T>(value) {

    fun update(value: T) {
        if (!blockSubsequentUpdate)
            postValue(
                convertValuePrePost(value)
            )
    }

    fun blockSubsequentUpdate() {
        blockSubsequentUpdate = true
    }

    private var blockSubsequentUpdate by AutoSwitch(value = false, switchOn = true)
}