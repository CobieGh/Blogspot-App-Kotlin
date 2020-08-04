package com.sample.blospot.di.auth

import androidx.lifecycle.ViewModel
import com.sample.blospot.di.ViewModelKey
import com.sample.blospot.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}
