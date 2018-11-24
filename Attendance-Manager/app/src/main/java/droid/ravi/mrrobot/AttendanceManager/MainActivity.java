package droid.ravi.mrrobot.AttendanceManager;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ListView listView;
    SubjectCursorAdapter subjectCursorAdapter;
    private static final int SUB_LOADER = 0;
    private PendingIntent pendingIntent;
    private SharedPreferences pref;
    MenuItem checkable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartAppSDK.init(this, "208093105", false);
        StartAppAd.disableSplash();

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReciever.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        // getting shared preferences
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        setDefaultPercentage(75);

        // starting alarm
        if(pref.getBoolean("is_checked", true))
            startAlarm();

        // Listview and passing cursor to its adapter
        listView = (ListView)findViewById(R.id.listview);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        subjectCursorAdapter = new SubjectCursorAdapter(this,null,0);
        listView.setAdapter(subjectCursorAdapter);

        // kickoff loader
        getSupportLoaderManager().initLoader(SUB_LOADER,null,this);


        listView.setClickable(true);
        //    listview Long item click listener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                // Create an AlertDialog.Builder and set the message, and click listeners
                // for the postivie and negative buttons on the dialog.
                final Long  subid = id;

                CharSequence c[]={"Edit","Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(c, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==1)
                        {
                            Log.i("isdfghjkd", String.valueOf(subid));
                            long ide = subid;
                            deletealert(ide);
                        }
                        else if (i==0){
                            long ide = subid;
                            Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                            intent.setData(SubjectContract.SubjectEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(ide)).build());
                            startActivity(intent);
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });


    }

    // Deleting item in the database
    void deleteSubject(long id){
        long ids =id;
        ContentValues values = new ContentValues();
        values.put(SubjectContract.SubjectEntry._ID, ids);

        int rowsdeleted = getContentResolver().delete(SubjectContract.SubjectEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(ids)).build(),null,null);

        // Show a toast message depending on whether or not the insertion was successful
        if (rowsdeleted == 0) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "File does not exists", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
           // loaddata();
            Toast.makeText(this, "Subject Deleted Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    void deletealert(long id){
        final Long ids = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.AlertMessage);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Log.i("call", String.valueOf(ids));
                deleteSubject(ids);
            }
        });
        builder.setNegativeButton(R.string.nosorry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Loader

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[]={
                SubjectContract.SubjectEntry._ID,
                SubjectContract.SubjectEntry.COLUMN_CLSATND,
                SubjectContract.SubjectEntry.COLUMN_SUB_NAME,
                SubjectContract.SubjectEntry.COLUMN_TTLCLS
        };
        return new CursorLoader(this,
                SubjectContract.SubjectEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        subjectCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subjectCursorAdapter.swapCursor(null);
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        checkable = menu.findItem(R.id.reminder);
        checkable.setChecked(pref.getBoolean("is_checked",true));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.about:
                  startActivity(new Intent(MainActivity.this,AboutActivity.class));
                  break;
            case R.id.changePercentage:
                changePercentageDialogBox();
                break;
            case R.id.reminder:
                changeReminderOption();
                break;
            case R.id.Add_Subject:
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 60 * 12, pendingIntent);
    }


    void setDefaultPercentage(Integer percent){
        if (pref.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            pref.edit().putInt("requiredPercentage", percent).apply();
            pref.edit().putBoolean("is_checked", true).apply();
            // record the fact that the app has been started at least once
            pref.edit().putBoolean("my_first_time", false).apply();
        }
    }

    void changePercentageDialogBox(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter Required Percentage");
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);
        input.setText(String.valueOf(pref.getInt("requiredPercentage",75)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,10);
        input.setLayoutParams(lp);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int percent = Integer.valueOf(input.getText().toString());
                pref.edit().putInt("requiredPercentage", percent).apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setView(input);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void changeReminderOption(){
        if(checkable.isChecked()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("  Alert !!");
            builder.setMessage("Reminder is important to get alert for updating attendance and avoiding low attendance .");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    pref.edit().putBoolean("is_checked",false).apply();
                    cancelAlarm();
                    checkable.setChecked(pref.getBoolean("is_checked",true));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            pref.edit().putBoolean("is_checked",true).apply();
            checkable.setChecked(pref.getBoolean("is_checked",true));
            Toast.makeText(this, "Notifications Started", Toast.LENGTH_SHORT).show();
            startAlarm();
        }
    }

    public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Notifications Canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }

}
