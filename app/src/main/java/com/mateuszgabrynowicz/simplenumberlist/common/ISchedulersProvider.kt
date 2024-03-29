package com.mateuszgabrynowicz.simplenumberlist.common

import io.reactivex.Scheduler

/**
 * Created by Mateusz on 19.05.2019.
 */
interface ISchedulersProvider {
    fun io(): Scheduler
    fun mainThread(): Scheduler
}