package com.mateuszgabrynowicz.simplenumberlist.di

import com.mateuszgabrynowicz.simplenumberlist.common.DefaultSchedulersProvider
import com.mateuszgabrynowicz.simplenumberlist.common.SchedulersProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Mateusz on 19.05.2019.
 */

@Module
object RxModule {
    @Provides
    @JvmStatic
    fun providesSchedulersProvider(schedulersProvider: DefaultSchedulersProvider): SchedulersProvider {
        return schedulersProvider
    }

    @Provides
    @JvmStatic
    fun providesCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}