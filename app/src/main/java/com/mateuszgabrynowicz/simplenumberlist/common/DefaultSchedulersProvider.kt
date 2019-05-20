package com.mateuszgabrynowicz.simplenumberlist.common

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Mateusz on 19.05.2019.
 */

class DefaultSchedulersProvider @Inject constructor() : ISchedulersProvider {
    override fun io() = Schedulers.io()

    override fun mainThread() = AndroidSchedulers.mainThread()
}