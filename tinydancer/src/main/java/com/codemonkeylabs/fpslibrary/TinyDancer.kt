package com.codemonkeylabs.fpslibrary

import android.app.Application
import android.view.Choreographer
import com.codemonkeylabs.fpslibrary.ui.TinyCoach

class TinyDancer {

    private lateinit var config: FPSConfig
    private lateinit var tinyCoach: TinyCoach
    private lateinit var fpsFrameCallback: FPSFrameCallback

    fun show() {
        tinyCoach.show()
    }

    fun hide() {
        tinyCoach.hide(false)
    }

    object Builder {

        private lateinit var tinyDancer: TinyDancer

        fun create(application: Application): TinyDancer {
            tinyDancer = TinyDancer()

            tinyDancer.apply {
                config = FPSConfig()
                tinyCoach = TinyCoach(application, config)
                fpsFrameCallback = FPSFrameCallback(config, tinyCoach)
            }

            Choreographer.getInstance().postFrameCallback(tinyDancer.fpsFrameCallback)
            return tinyDancer
        }
    }
}