package droid.ravi.mrrobot.AttendanceManager;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalActivity extends AppCompatActivity {
    GridView calendarView;
    TextView txtDate;
    LinearLayout header;
    ImageView btnPrev, btnNext;
    ArrayList<String> cells ;
    ArrayList<Integer> markthese;
    Calendar currentDate;
    Button saveEvent,delete_event;
    String selectedDate = " ";
    View pre=null;
    int preColor;
    EditText note_edittext;
    int flag =0;  // 0 save and 1 update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        calendarView = findViewById(R.id.calendarGrid);
        header =  findViewById(R.id.calendar_header);
        btnPrev = findViewById(R.id.calendar_prev_button);
        btnNext = findViewById(R.id.calendar_next_button);
        txtDate = findViewById(R.id.calendar_date_display);
        saveEvent = findViewById(R.id.save_event);
        delete_event = findViewById(R.id.delete_event);
        note_edittext =  findViewById(R.id.note_edittext);
        currentDate = Calendar.getInstance();
        updateCalendar();
        setClickListener();
        assignClickHandlers();
    }

    private void setClickListener(){
        // click listener
        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (pre!=null)
                {
                    //pre.setBackgroundColor(getResources().getColor(R.color.white));
                    ((TextView)pre).setTextColor(preColor);  // setting text colour of previously selected day to normal
                }

                selectedDate = String.valueOf(cells.get(position));

                if(selectedDate!=null && !selectedDate.isEmpty()){

                    pre = v;
                    preColor = ((TextView)v).getCurrentTextColor(); // storing text colour of current selected day to restore it when it is not selected
                    ((TextView)v).setTextColor(Color.parseColor("#ff0000"));
                    if(markthese.get(position)==1){  // if current date has any event saved
                        saveEvent.setText(R.string.Update);
                        updateEditText(selectedDate);
                        delete_event.setVisibility(View.VISIBLE);
                        flag=1;
                    }
                    else{
                        saveEvent.setText(R.string.Save);
                        delete_event.setVisibility(View.INVISIBLE);
                        note_edittext.setText(R.string.add_note);
                        flag=0;
                    }
                }
            }
        });
    }

    private void updateEditText(String selectedDate) { // updating edittext to saved text in database
        String text = query(selectedDate);
        note_edittext.setText(text);
    }


    private void updateCalendar() {  // setting up calendar
        cells = new ArrayList<>();
        markthese = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells (42 days calendar as per our business logic)
        while (cells.size() < 42) {
            Date temp = calendar.getTime();
            String[] arr = temp.toString().split(" ");
            String d;
            d = arr[2]+" "+arr[1]+" "+arr[5];
            cells.add(d);
            markthese.add(hasNote(d));

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        /*for(Integer d : markthese)
            Log.i("list   ", String.valueOf(d));*/
        // update grid
        calendarView.setAdapter(new CalendarAdapter(this, 0, cells , currentDate.get(Calendar.MONTH) , markthese ,currentDate.get(Calendar.YEAR)));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        txtDate.setText(sdf.format(currentDate.getTime()));
    }

    private void assignClickHandlers() {
        // add one month and refresh UI
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // updating calendar to next month
                currentDate.add(Calendar.MONTH, 1);
                Log.i("current month", String.valueOf(currentDate.get(Calendar.MONTH)));
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new View.OnClickListener() { // updating calendar to previous month
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                Log.i("current month", String.valueOf(currentDate.get(Calendar.MONTH)));
                updateCalendar();
            }
        });
    }

    String query(String day){  // getting note from database
        ContentValues values = new ContentValues();
        values.put(CalContract.CalEntry.COLUMN_DAY, day);

        Cursor c;
        c = getContentResolver().query(CalContract.CalEntry.CONTENT_URI.buildUpon().appendPath(day).build(),null,null, new String[]{day},null,null);
        if(c!=null && c.getCount()>0)
        {
            c.moveToFirst();
            Log.i("from database",c.getString(1));
            String fromdb = c.getString(1);
            c.close();
            return fromdb;
        }
        else
            return "null";

    }

    void update(String note,String day){  // updating note in database
        ContentValues values = new ContentValues();
        values.put(CalContract.CalEntry.COLUMN_NOTE, note);
        Integer rowsupdated = getContentResolver().update(CalContract.CalEntry.CONTENT_URI,values,"day=?",new String[]{day});
        Log.i("rowsupdated", String.valueOf(rowsupdated));
        if (rowsupdated<0) {
            Toast.makeText(this, "Invalid Data",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note Updated .",
                    Toast.LENGTH_SHORT).show();
        }
        updateCalendar();
    }

    private void insert(String day ,String note){ // inserting new note in database

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CalContract.CalEntry.COLUMN_DAY, day);
        values.put(CalContract.CalEntry.COLUMN_NOTE, note);

        Uri newUri;

        newUri = getContentResolver().insert(CalContract.CalEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, "Invalid Data",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note Saved .",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private  void deletenote(String day){

        int rowsdeleted = getContentResolver().delete(CalContract.CalEntry.CONTENT_URI, CalContract.CalEntry.COLUMN_DAY+"=?", new String[]{day});

        // Show a toast message depending on whether or not the insertion was successful
        if (rowsdeleted == 0) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "File does not exists", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            // loaddata();
            Toast.makeText(this, "Note Deleted .", Toast.LENGTH_SHORT).show();
        }
        note_edittext.setText(R.string.add_note);
        updateCalendar();
        selectedDate=" ";
    }


    public void saveEvent(View view) {  // on clicking save button either save or update

        if(selectedDate.equals(" ")){
            errorToast();
            return;
        }

        String text = String.valueOf(note_edittext.getText());
        if(!text.isEmpty()) {
            if (flag == 0) {

                insert(selectedDate, text);
                updateCalendar();

            } else {
                update(text, selectedDate);
            }
        }
        selectedDate=" ";
    }

    int hasNote(String day){  // checking if the current date has an event
        Cursor c = null;
        try{
            ContentValues values = new ContentValues();
            values.put(CalContract.CalEntry.COLUMN_DAY, day);


            c = getContentResolver().query(CalContract.CalEntry.CONTENT_URI.buildUpon().appendPath(day).build(),null,null, new String[]{day},null,null);
            if(c!=null && c.getCount()>0){
                c.close();
                return 1;
            }
            assert c != null;
            c.close();
            return 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteEvent(View view) {
        if(selectedDate.equals(" ")){
            errorToast();
            return;
        }
        deletenote(selectedDate);
        delete_event.setVisibility(View.INVISIBLE);
        saveEvent.setText(R.string.Save);    }

    public void errorToast(){
        Toast.makeText(this, "Please click on a date to select it ", Toast.LENGTH_LONG).show();
    }
}
