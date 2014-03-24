package com.rikardlegge.gymnasticsapplication;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

public class NotificationService extends Service {

	final static String ACTION = "NotifyServiceAction";
	final static String STOP_SERVICE = "";
	final static int RQS_STOP_SERVICE = 1;
	public ArrayList<Float> eventTimers;
	public ArrayList<String> links;
	Calendar cal = Calendar.getInstance();

	NotifyServiceReceiver notifyServiceReceiver;

	private Notification myNotification;

	@Override
	public void onCreate() {
		notifyServiceReceiver = new NotifyServiceReceiver();
		super.onCreate();
	}

	public float timeToDecimal(float time) {
		float deciTmp = Math.round(time - .5);
		return (float) (deciTmp + (time - deciTmp) * 5 / 3);
	}

	@SuppressWarnings("unchecked")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			eventTimers = (ArrayList<Float>) intent.getExtras().get("timers");
			links = (ArrayList<String>) intent.getExtras().get("links");
		}

		Thread timer = new Thread() {
			public void run() {
				try {
					int i = 0;
					while (i < eventTimers.size()) {
						float curTime = timeToDecimal(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 100f);
						for (int j = i; j < eventTimers.size(); j++) {
							if (eventTimers.get(j) > curTime) {
								i = j;
								break;
							}
						}
						float diff = eventTimers.get(i) - curTime;
						// System.out.println(diff + "/" + 60 * 60 * ((int)
						// (diff * 1000)));
						sleep(60 * 60 * ((int) (diff * 1000)));

						postNotification(links.get(i));
						i += 1;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};
		timer.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressWarnings("deprecation")
	public void postNotification(String link) {
		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		if (cal.get(Calendar.MINUTE) < 10)
			minute = "0" + minute;
		String notificationTmpTitle = "Klockan �r " + cal.get(Calendar.HOUR_OF_DAY) + ":" + minute;
		String notificationTitle = "Nu �r det dags";
		String notificationText = "Starta tr�ningspasset!";

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION);
		registerReceiver(notifyServiceReceiver, intentFilter);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Send Notification notificationManager = (NotificationManager)
		getSystemService(Context.NOTIFICATION_SERVICE);
		myNotification = new Notification(R.drawable.mbicon, notificationTmpTitle, System.currentTimeMillis());
		Context context = getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + link));
		intent.putExtra("VIDEO_ID", link);
		PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		myNotification.defaults |= Notification.DEFAULT_SOUND;
		myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);
		notificationManager.notify(0, myNotification);
		stopSelf();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(notifyServiceReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public class NotifyServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int rqs = arg1.getIntExtra("RQS", 0);
			if (rqs == RQS_STOP_SERVICE) {
				stopSelf();
			}
		}
	}

}