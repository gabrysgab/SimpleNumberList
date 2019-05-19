package com.mateuszgabrynowicz.simplenumberlist.di

import com.mateuszgabrynowicz.simplenumberlist.numbers_list.NumbersListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Mateusz on 19.05.2019.
 */

@Module
internal abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun contributesMainActivity(): NumbersListActivity
}