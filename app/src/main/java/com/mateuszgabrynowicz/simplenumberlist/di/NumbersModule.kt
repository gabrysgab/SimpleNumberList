package com.mateuszgabrynowicz.simplenumberlist.di

import com.mateuszgabrynowicz.simplenumberlist.common.ISchedulersProvider
import com.mateuszgabrynowicz.simplenumberlist.repository.INumbersRepository
import com.mateuszgabrynowicz.simplenumberlist.repository.NumbersRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable

/**
 * Created by Mateusz on 19.05.2019.
 */

@Module
object NumbersModule {

    @Provides
    @JvmStatic
    @Reusable
    fun providesNumbersRepository(schedulersProvider: ISchedulersProvider): INumbersRepository {
        return NumbersRepository(schedulersProvider)
    }
}