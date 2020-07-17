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

    private lateinit var config: FPSConfig
    private lateinit var tinyCoach: TinyCoach
    private lateinit var fpsFrameCallback: FPSFrameCallback

    fun show(context: Context) {
        if (overlayPermRequest(context)) {
            //once permission is granted then you must call show() again
            return
        }

        tinyCoach.show()
    }

    private fun overlayPermRequest(context: Context): Boolean {
        var permNeeded = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                permNeeded = true
            }
        }
        return permNeeded
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