package com.mateuszgabrynowicz.simplenumberlist.di

import com.mateuszgabrynowicz.simplenumberlist.common.DefaultSchedulersProvider
import com.mateuszgabrynowicz.simplenumberlist.common.ISchedulersProvider
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Mateusz on 19.05.2019.
 */

@Module
object RxModule {

    @Provides
    @JvmStatic
    @Reusable
    fun providesSchedulersProvider(schedulersProvider: DefaultSchedulersProvider): ISchedulersProvider {
        return schedulersProvider
    }

    @Provides
    @JvmStatic
    fun providesCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}