package com.managerslog.app;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
public class ReminderReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context ctx,Intent intent){
  if(Build.VERSION.SDK_INT>=33&&ctx.checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED)return;
  NotificationManager nm=(NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel("tasks","Task reminders",NotificationManager.IMPORTANCE_DEFAULT));
  Intent open=new Intent(ctx,MainActivity.class);PendingIntent pi=PendingIntent.getActivity(ctx,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(ctx,"tasks"):new Notification.Builder(ctx);
  b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Manager's Log reminder").setContentText(intent.getStringExtra("title")).setAutoCancel(true).setContentIntent(pi);
  nm.notify(intent.getStringExtra("id").hashCode(),b.build());
 }
}
