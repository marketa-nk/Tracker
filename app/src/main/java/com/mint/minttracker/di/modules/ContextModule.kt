package com.mint.minttracker.di.modules

import android.content.Context
import com.mint.minttracker.di.components.AppScope
import dagger.Module
import dagger.Provides

@Module
class ContextModule (private val context: Context) {

    @AppScope
    @Provides
    fun context(): Context {
        return context.applicationContext
    }
}
