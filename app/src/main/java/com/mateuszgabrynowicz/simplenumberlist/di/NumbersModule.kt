package com.mateuszgabrynowicz.simplenumberlist.di

import com.mateuszgabrynowicz.simplenumberlist.common.SchedulersProvider
import com.mateuszgabrynowicz.simplenumberlist.repository.INumbersRepository
import com.mateuszgabrynowicz.simplenumberlist.repository.NumbersRepository
import dagger.Module
import dagger.Provides

/**
 * Created by Mateusz on 19.05.2019.
 */

@Module
object NumbersModule {

    @Provides
    @JvmStatic
    fun providesNumbersRepository(schedulersProvider: SchedulersProvider): INumbersRepository {
        return NumbersRepository(schedulersProvider)
    }
}