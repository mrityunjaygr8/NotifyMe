package gr8.mrityunjay.notifyme

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "gr8.mrityunjay.notifyme.notification"
        const val NOTIFICATION_ID = 0
        const val GUIDE_URL = "https://developer.android.com/design/patterns/notifications.html"
        const val ACTION_UPDATE_NOTIFICATION =
        "gr8.mrityunjay.notifyme.ACTION_UPDATE_NOTIFICATION"
    }

    private lateinit var mNotifyButton: Button
    private lateinit var mUpdateButton: Button
    private lateinit var mCancelButton: Button
    lateinit var mNotifyManager: NotificationManager
    private var mReceiver: NotificationReceiver = NotificationReceiver()

    inner class NotificationReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNotifyButton = notify
        mUpdateButton = update
        mCancelButton = cancel

        mNotifyButton.setOnClickListener({ sendNotification() })
        mUpdateButton.setOnClickListener({ updateNotification() })
        mCancelButton.setOnClickListener({ cancelNotification() })

        mNotifyButton.isEnabled = true
        mUpdateButton.isEnabled = true
        mCancelButton.isEnabled = true

        mNotifyManager = this.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChanel(CHANNEL_ID, getString(R.string.notification_name), getString(R.string.notification_description))

        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    private fun createNotificationChanel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.DKGRAY
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 100, 100, 200, 100, 300, 100, 400, 100)
        mNotifyManager.createNotificationChannel(channel)
    }

    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val learnMoreIcon: Icon = Icon.createWithResource(this, R.drawable.ic_info_icon)

        val learnMoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GUIDE_URL))
        val learnMorePendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, learnMoreIntent, PendingIntent.FLAG_ONE_SHOT)
        val learnMoreAction: Notification.Action = Notification.Action.Builder(learnMoreIcon, getString(R.string.learn_more), learnMorePendingIntent).build()

        val updateIcon = Icon.createWithResource(this, R.drawable.ic_update_icon)
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast( this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val updateAction = Notification.Action.Builder(updateIcon, getString(R.string.update), updatePendingIntent).build()

        val notification = Notification.Builder(this@MainActivity, CHANNEL_ID)
                .setContentText(getString(R.string.notification_text))
                .setContentTitle(getString(R.string.notifcation_title))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(notificationPendingIntent)
                .addAction(learnMoreAction)
                .addAction(updateAction)
                .setChannelId(CHANNEL_ID)
                .build()

        mNotifyButton.isEnabled = false
        mUpdateButton.isEnabled = true
        mCancelButton.isEnabled = true


        try {
            mNotifyManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateNotification() {
        val androidImage: Bitmap = BitmapFactory.decodeResource(resources , R.drawable.mascot_1)
        val intent = Intent(this, MainActivity::class.java)

        val learnMoreBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_info_icon)
        val learnMoreIcon: Icon = Icon.createWithBitmap(learnMoreBitmap)

        val learnMoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GUIDE_URL))
        val learnMorePendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, learnMoreIntent, PendingIntent.FLAG_ONE_SHOT)
        val learnMoreAction: Notification.Action = Notification.Action.Builder(learnMoreIcon, getString(R.string.learn_more), learnMorePendingIntent).build()

        val notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = Notification.Builder(this@MainActivity, CHANNEL_ID)
                .setContentText(getString(R.string.notification_text))
                .setContentTitle(getString(R.string.notifcation_title))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(notificationPendingIntent)
                .setChannelId(CHANNEL_ID)
                .addAction(learnMoreAction)
                .setStyle(Notification.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle(getString(R.string.notification_updated)))
                .build()

        mNotifyButton.isEnabled = false
        mUpdateButton.isEnabled = false
        mCancelButton.isEnabled = true


        try {
            mNotifyManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID)
        mNotifyButton.isEnabled = true
        mUpdateButton.isEnabled = false
        mCancelButton.isEnabled = false

    }
}
