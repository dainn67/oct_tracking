package com.oceantech.tracking.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.oceantech.tracking.ui.client.home.ClientHomeFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FragmentModule {
    @Binds
    fun bindFragmentFactory(factory: VectorFragmentFactory): FragmentFactory
    @Binds
    @IntoMap
    @FragmentKey(ClientHomeFragment::class)
    fun bindHomeFragment(clientHomeFragment: ClientHomeFragment): Fragment

}