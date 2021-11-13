package com.barryalan.winetraining.util

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<MutableList<T>>.addNewItem(item: T) {
    val oldValue = this.value ?: mutableListOf()
    if (!oldValue.contains(item)) {
        oldValue.add(item)
        this.value = oldValue
    }
}

fun <T> MutableLiveData<MutableList<T>>.removeItem(item: T) {
    if (!this.value.isNullOrEmpty()) {
        val oldValue = this.value
        oldValue?.remove(item)
        this.value = oldValue
    } else {
        this.value = mutableListOf()
    }
}

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}