package com.managerslog.app;
import android.app.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.webkit.*;
import android.widget.Toast;
import android.provider.DocumentsContract;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
public class MainActivity extends Activity {
 WebView web; ValueCallback<Uri[]> photoCallback; static final int PHOTO=1,FOLDER=2,RESTORE=3;
 Handler main=new Handler(Looper.getMainLooper()); final java.util.concurrent.ExecutorService backups=java.util.concurrent.Executors.newSingleThreadExecutor();
 @Override public void onCreate(Bundle b){super.onCreate(b);web=new WebView(this);setContentView(web);WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);web.setWebViewClient(new WebViewClient());web.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView w,ValueCallback<Uri[]> cb,FileChooserParams p){photoCallback=cb;Intent i=new Intent(Intent.ACTION_GET_CONTENT);i.setType("image/*");startActivityForResult(Intent.createChooser(i,"Choose photo"),PHOTO);return true;}});web.addJavascriptInterface(new Bridge(),"Android");web.loadUrl("file:///android_asset/index.html");if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},55);}
 @Override protected void onResume(){super.onResume();if(web!=null)web.evaluateJavascript("window.onAppResume&&onAppResume()",null);}
 void msg(String s){main.post(()->Toast.makeText(this,s,Toast.LENGTH_LONG).show());}
 @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(req==PHOTO){if(photoCallback!=null){photoCallback.onReceiveValue(result==RESULT_OK&&data!=null?new Uri[]{data.getData()}:null);photoCallback=null;}return;}if(result!=RESULT_OK||data==null)return;Uri u=data.getData();if(req==FOLDER){try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);getPreferences(0).edit().putString("folder",u.toString()).apply();web.evaluateJavascript("window.backupNow&&backupNow()",null);}catch(Exception e){msg("Folder access failed");}}if(req==RESTORE){try(InputStream in=getContentResolver().openInputStream(u)){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);String json=out.toString("UTF-8");new JSONObject(json);web.evaluateJavascript("restoreData("+JSONObject.quote(json)+")",null);}catch(Exception e){msg("Restore failed");}}}
 public class Bridge {
 @JavascriptInterface public void chooseFolder(){main.post(()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(i,FOLDER);});}
 @JavascriptInterface public void restore(){main.post(()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,RESTORE);});}
 @JavascriptInterface public void email(String subject,String body){main.post(()->{Intent i=new Intent(Intent.ACTION_SEND);i.setType("message/rfc822");i.putExtra(Intent.EXTRA_SUBJECT,subject);i.putExtra(Intent.EXTRA_TEXT,body);startActivity(Intent.createChooser(i,"Email Store Walk"));});}
 @JavascriptInterface public void backup(String data){backups.execute(()->{try{String folder=getPreferences(0).getString("folder",null);if(folder==null)return;Uri root=Uri.parse(folder);String id=DocumentsContract.getTreeDocumentId(root);Uri parent=DocumentsContract.buildDocumentUriUsingTree(root,id);Uri children=DocumentsContract.buildChildDocumentsUriUsingTree(root,id);Uri old=null;try(android.database.Cursor c=getContentResolver().query(children,new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID,DocumentsContract.Document.COLUMN_DISPLAY_NAME},null,null,null)){if(c!=null)while(c.moveToNext())if("managers-log-backup.json".equals(c.getString(1)))old=DocumentsContract.buildDocumentUriUsingTree(root,c.getString(0));}Uri fresh=DocumentsContract.createDocument(getContentResolver(),parent,"application/json","managers-log-backup-pending.json");try(OutputStream out=getContentResolver().openOutputStream(fresh,"wt")){out.write(data.getBytes(StandardCharsets.UTF_8));out.flush();}if(old!=null)DocumentsContract.deleteDocument(getContentResolver(),old);DocumentsContract.renameDocument(getContentResolver(),fresh,"managers-log-backup.json");}catch(Exception e){msg("Automatic backup failed");}});}
 @JavascriptInterface public void reminder(String id,String title,long at){Intent i=new Intent(MainActivity.this,ReminderReceiver.class);i.setAction("REMIND");i.putExtra("id",id);i.putExtra("title",title);PendingIntent p=PendingIntent.getBroadcast(MainActivity.this,id.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);AlarmManager a=(AlarmManager)getSystemService(ALARM_SERVICE);a.cancel(p);if(at>System.currentTimeMillis())a.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,p);}
 @JavascriptInterface public void cancelReminder(String id){Intent i=new Intent(MainActivity.this,ReminderReceiver.class);i.setAction("REMIND");PendingIntent p=PendingIntent.getBroadcast(MainActivity.this,id.hashCode(),i,PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);if(p!=null){((AlarmManager)getSystemService(ALARM_SERVICE)).cancel(p);p.cancel();}}
 }
}
