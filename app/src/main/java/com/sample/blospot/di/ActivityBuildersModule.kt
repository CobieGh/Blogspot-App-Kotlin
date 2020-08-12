package com.sample.blospot.di

import com.sample.blospot.di.auth.AuthFragmentBuildersModule
import com.sample.blospot.di.auth.AuthModule
import com.sample.blospot.di.auth.AuthScope
import com.sample.blospot.di.auth.AuthViewModelModule
import com.sample.blospot.di.main.MainFragmentBuildersModule
import com.sample.blospot.di.main.MainModule
import com.sample.blospot.di.main.MainScope
import com.sample.blospot.di.main.MainViewModelModule
import com.sample.blospot.ui.auth.AuthActivity
import com.sample.blospot.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}