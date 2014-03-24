package com.rikardlegge.gymnasticsapplication;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

// The class has to extend the BroadcastReceiver to get the notification from the system
public class TimeAlarm extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent paramIntent) {
		String link = "";
		if (paramIntent != null) {
			link = (String) paramIntent.getExtras().get("link");
			if (link == null)
				return;
		}

		Calendar calendar = Calendar.getInstance();
		String minute = String.valueOf(calendar.get(Calendar.MINUTE));
		if (calendar.get(Calendar.MINUTE) < 10)
			minute = "0" + minute;

		String notificationPupupTitle = "Klockan är " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute;
		String notificationTitle = "Nu är det dags";
		String notificationText = "Starta träningspasset!";

		// Request the notification manager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Create a new intent which will be fired if you click on the
		// notification
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + link));
		intent.putExtra("VIDEO_ID", link);

		// Attach the intent to a pending intent
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Create the notification
		Notification notification = new Notification(R.drawable.mbicon, notificationPupupTitle, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.ledARGB |= Color.BLUE;
		notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

		// Fire the notification
		notificationManager.notify(1, notification);
	}
}