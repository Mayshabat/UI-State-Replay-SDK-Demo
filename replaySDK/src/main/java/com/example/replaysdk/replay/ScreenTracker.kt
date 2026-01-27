package com.example.replaysdk.replay

import android.app.Activity
import android.app.Application
import android.os.Bundle

object ScreenTracker {

    fun install(app: Application) {
        app.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityResumed(activity: Activity) {
                // שם המסך = שם ה-Activity
                val screenName = activity::class.java.simpleName
                Replay.trackScreen(screenName)
            }

            override fun onActivityCreated(a: Activity, b: Bundle?) {}
            override fun onActivityStarted(a: Activity) {}
            override fun onActivityPaused(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
            override fun onActivityDestroyed(a: Activity) {}
        })
    }
}
