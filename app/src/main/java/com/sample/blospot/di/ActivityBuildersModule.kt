package com.sample.blospot.di

import com.sample.blospot.di.auth.AuthFragmentBuildersModule
import com.sample.blospot.di.auth.AuthModule
import com.sample.blospot.di.auth.AuthScope
import com.sample.blospot.di.auth.AuthViewModelModule
import com.sample.blospot.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

}