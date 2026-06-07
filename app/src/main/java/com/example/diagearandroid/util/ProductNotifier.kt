package com.example.diagearandroid.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.diagearandroid.R
import java.util.concurrent.atomic.AtomicInteger

/**
 * Posts a local notification whenever a product is added, edited, or deleted.
 *
 * Kept deliberately simple (no FCM). Strings are resolved through [LocaleHelper.wrap] so the
 * notification matches the user's chosen in-app language even when posted from an application
 * context.
 */
object ProductNotifier {
    private const val CHANNEL_ID = "product_changes"

    /** Unique id per notification so each event shows its own entry instead of replacing the last. */
    private val nextId = AtomicInteger(1000)

    enum class Action { ADDED, UPDATED, DELETED }

    fun notify(context: Context, action: Action, productName: String) {
        val ctx = LocaleHelper.wrap(context)
        ensureChannel(ctx)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return // No permission — silently skip.
        }

        val (titleRes, textRes) = when (action) {
            Action.ADDED -> R.string.notif_product_added_title to R.string.notif_product_added_text
            Action.UPDATED -> R.string.notif_product_updated_title to R.string.notif_product_updated_text
            Action.DELETED -> R.string.notif_product_deleted_title to R.string.notif_product_deleted_text
        }

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.diagear_logo)
            .setContentTitle(ctx.getString(titleRes))
            .setContentText(ctx.getString(textRes, productName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(ctx).notify(nextId.getAndIncrement(), notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_channel_desc)
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
