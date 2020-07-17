package com.codemonkeylabs.fpslibrary

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Choreographer
import com.codemonkeylabs.fpslibrary.ui.TinyCoach

class TinyDancer {

    private var isInitialized = false
    private lateinit var config: FPSConfig
    private lateinit var tinyCoach: TinyCoach
    private lateinit var fpsFrameCallback: FPSFrameCallback

    fun show(context: Context) {
        if (isInitialized && isOverlayAllowed(context)) tinyCoach.show()
        else if (!isInitialized) {
            config = FPSConfig()
            tinyCoach = TinyCoach(context.applicationContext as Application, config)
            fpsFrameCallback = FPSFrameCallback(config, tinyCoach)

            Choreographer.getInstance().postFrameCallback(fpsFrameCallback)
        }
    }

    internal fun isOverlayAllowed(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context).also { isAllowed ->
                if (!isAllowed) Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).let { context.startActivity(it) }
            }
        } else true

    fun hide() {
        tinyCoach.hide(false)
    }

    object Builder {

        private lateinit var tinyDancer: TinyDancer

        fun create(application: Application): TinyDancer {
            tinyDancer = TinyDancer()

            if (tinyDancer.isOverlayAllowed(application)) {
                tinyDancer.apply {
                    config = FPSConfig()
                    tinyCoach = TinyCoach(application, config)
                    fpsFrameCallback = FPSFrameCallback(config, tinyCoach)
                }

                Choreographer.getInstance().postFrameCallback(tinyDancer.fpsFrameCallback)

                tinyDancer.isInitialized = true
            } else tinyDancer

            return tinyDancer
        }
    }
}