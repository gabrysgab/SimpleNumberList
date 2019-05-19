package com.mateuszgabrynowicz.simplenumberlist.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mateuszgabrynowicz.simplenumberlist.numbers_list.NumbersListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Created by Mateusz on 19.05.2019.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(NumbersListViewModel::class)
    abstract fun bindNumberListViewModel(numberListViewModel: NumbersListViewModel): ViewModel

    @Singleton
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}