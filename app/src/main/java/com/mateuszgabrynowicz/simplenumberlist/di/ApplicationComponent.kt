package com.mateuszgabrynowicz.simplenumberlist.di

import com.mateuszgabrynowicz.simplenumberlist.Application
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by Mateusz on 19.05.2019.
 */

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        NumbersModule::class,
        RxModule::class]
)
interface ApplicationComponent : AndroidInjector<Application> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<Application>()
}