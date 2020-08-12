package com.sample.blospot.di.main

import com.sample.blospot.ui.main.account.AccountFragment
import com.sample.blospot.ui.main.account.ChangePasswordFragment
import com.sample.blospot.ui.main.account.UpdateAccountFragment
import com.sample.blospot.ui.main.blog.BlogFragment
import com.sample.blospot.ui.main.blog.UpdateBlogFragment
import com.sample.blospot.ui.main.blog.ViewBlogFragment
import com.sample.blospot.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

}