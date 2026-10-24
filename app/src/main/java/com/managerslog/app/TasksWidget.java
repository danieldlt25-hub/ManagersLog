package com.managerslog.app;
import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.widget.*;
import android.view.View;
import org.json.*;
import java.util.*;

public class TasksWidget extends AppWidgetProvider {
 static final String PREF="widget_tasks",DATA="data",ACTIONS="pending",CHECK="com.managerslog.app.WIDGET_CHECK";
 static JSONArray read(Context c){try{return new JSONArray(c.getSharedPreferences(PREF,0).getString(DATA,"[]"));}catch(Exception e){return new JSONArray();}}
 static void update(Context c){AppWidgetManager m=AppWidgetManager.getInstance(c);android.content.ComponentName name=new android.content.ComponentName(c,TasksWidget.class);int[] ids=m.getAppWidgetIds(name);for(int id:ids){RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget_tasks);JSONArray a=read(c);v.setTextViewText(R.id.widget_count,a.length()+" open");Intent svc=new Intent(c,TasksWidgetService.class);svc.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);svc.setData(android.net.Uri.parse("widget://tasks/"+id));v.setRemoteAdapter(R.id.widget_list,svc);v.setEmptyView(R.id.widget_list,R.id.widget_empty);
 Intent add=new Intent(c,MainActivity.class).setAction("com.managerslog.app.QUICK_ADD").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
 v.setOnClickPendingIntent(R.id.widget_add,PendingIntent.getActivity(c,1,add,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));
 Intent open=new Intent(c,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);v.setOnClickPendingIntent(R.id.widget_title,PendingIntent.getActivity(c,2,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));
 Intent check=new Intent(c,TasksWidget.class).setAction(CHECK);v.setPendingIntentTemplate(R.id.widget_list,PendingIntent.getBroadcast(c,3,check,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE));
 m.updateAppWidget(id,v);m.notifyAppWidgetViewDataChanged(id,R.id.widget_list);
 }}
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){update(c);}
 @Override public void onReceive(Context c,Intent i){super.onReceive(c,i);if(CHECK.equals(i.getAction())){String id=i.getStringExtra("task_id");if(id==null)return;JSONArray old=read(c),next=new JSONArray();for(int j=0;j<old.length();j++){JSONObject n=old.optJSONObject(j);if(n!=null&&!id.equals(n.optString("id")))next.put(n);}android.content.SharedPreferences p=c.getSharedPreferences(PREF,0);String pending=p.getString(ACTIONS,"");if(!Arrays.asList(pending.split("\\n")).contains(id))pending+=id+"\n";p.edit().putString(DATA,next.toString()).putString(ACTIONS,pending).apply();update(c);}}
}
