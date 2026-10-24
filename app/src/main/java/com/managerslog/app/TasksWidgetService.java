package com.managerslog.app;
import android.content.*;
import android.widget.*;
import android.appwidget.*;
import org.json.*;
import java.util.*;
public class TasksWidgetService extends RemoteViewsService {
 @Override public RemoteViewsFactory onGetViewFactory(Intent intent){return new Factory(getApplicationContext());}
 static class Factory implements RemoteViewsFactory {
 final Context c;JSONArray items=new JSONArray();
 Factory(Context context){c=context;}
 public void onCreate(){onDataSetChanged();}
 public void onDataSetChanged(){items=TasksWidget.read(c);}
 public void onDestroy(){}
 public int getCount(){return items.length();}
 public RemoteViews getViewAt(int position){if(position<0||position>=items.length())return null;JSONObject n=items.optJSONObject(position);if(n==null)return null;RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget_task_row);v.setTextViewText(R.id.widget_task_text,(n.optBoolean("priority")?"★  ":"")+n.optString("text"));v.setTextViewText(R.id.widget_task_dept,n.optString("dept","General"));Intent fill=new Intent();fill.putExtra("task_id",n.optString("id"));v.setOnClickFillInIntent(R.id.widget_task_check,fill);return v;}
 public RemoteViews getLoadingView(){return null;}
 public int getViewTypeCount(){return 1;}
 public long getItemId(int position){return position;}
 public boolean hasStableIds(){return false;}
 }
}
