package com.mint.minttracker.extensions

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

// Set default value for any type of MutableLiveData
fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

// Set new value for any tye of MutableLiveData
fun <T> MutableLiveData<T>.set(newValue: T) = apply { setValue(newValue) }

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.fragmentViewModels(
    noinline ownerProducer: () -> ViewModelStore = { this.viewModelStore },
    crossinline creator: () -> T,
): Lazy<T> {
    return createViewModelLazy(
        viewModelClass = T::class,
        storeProducer = ownerProducer,
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = creator.invoke() as T
            }
        }
    )
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}
