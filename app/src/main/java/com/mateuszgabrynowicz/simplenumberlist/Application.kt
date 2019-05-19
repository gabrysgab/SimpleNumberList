package com.mateuszgabrynowicz.simplenumberlist

import com.mateuszgabrynowicz.simplenumberlist.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Created by Mateusz on 19.05.2019.
 */

class Application : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().create(this)
    }
}