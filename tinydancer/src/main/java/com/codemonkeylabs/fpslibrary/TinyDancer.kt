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
        if (isOverlayAllowed(context)) {
            if (isInitialized) tinyCoach.show()
            else {
                config = FPSConfig()
                tinyCoach = TinyCoach(context.applicationContext as Application, config)
                fpsFrameCallback = FPSFrameCallback(config, tinyCoach)

                Choreographer.getInstance().postFrameCallback(fpsFrameCallback)
            }
        }
    }

    private fun isOverlayAllowed(context: Context): Boolean =
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

        fun create(): TinyDancer {
            tinyDancer = TinyDancer()
            return tinyDancer
        }
    }
}