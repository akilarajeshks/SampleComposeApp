package com.plex.samplecomposeapp.modifierUtils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.Modifier

private val features = hashMapOf<String, Boolean>()
var appContext: Context? = null
inline fun Modifier.whenTrue(condition: Boolean, modifierScope: Modifier.() -> Modifier): Modifier {
    return if (condition) modifierScope() else this
}

inline fun Modifier.whenTV(
    modifierScope: Modifier.() -> Modifier,
    applicationContext: Context? = appContext
): Modifier {
    if (applicationContext != null && appContext == null) {
        appContext = applicationContext
    }
        return whenTrue(isTVDevice(applicationContext = appContext!!), modifierScope)
    }

    fun isTVDevice(applicationContext: Context? = appContext): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hasSystemFeature(PackageManager.FEATURE_LEANBACK_ONLY, applicationContext!!)
        } else {
            supportsLeanback(applicationContext!!)
        }
    }

    fun hasSystemFeature(feature: String, applicationContext: Context): Boolean {
        return features.getOrPut(feature) {
            applicationContext.packageManager.hasSystemFeature(
                feature
            )
        }
    }

    fun supportsLeanback(applicationContext: Context): Boolean {
        return hasSystemFeature("android.software.leanback", applicationContext)
    }