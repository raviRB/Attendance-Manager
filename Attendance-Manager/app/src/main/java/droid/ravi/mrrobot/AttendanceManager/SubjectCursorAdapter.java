package droid.ravi.mrrobot.AttendanceManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SubjectCursorAdapter extends CursorAdapter {


    public SubjectCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView detailTextView = (TextView) view.findViewById(R.id.summary);
        ImageButton incrementButton = (ImageButton) view.findViewById(R.id.attIncr);
        ImageButton decrementButton = (ImageButton) view.findViewById(R.id.attddecr);
        incrementButton.setTag(cursor.getInt(0));  // set id as tag with the view
        decrementButton.setTag(cursor.getInt(0));

        // Find the columns of subject attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUB_NAME);
        int ttlclsColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TTLCLS);
        int clsatndColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLSATND);


        String subName = cursor.getString(nameColumnIndex);
        final Integer totslClasses =  cursor.getInt(ttlclsColumnIndex);
        final Integer totslClassesAttended =  cursor.getInt(clsatndColumnIndex);
        String percent = getpercent(totslClassesAttended,totslClasses);
        String tv1 = subName+" - "+percent+"%";
        nameTextView.setText(tv1);
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        Integer reqPercent = pref.getInt("requiredPercentage",75);

        if(Float.valueOf(percent)<reqPercent){
            Log.i("req",String.valueOf(Float.valueOf(percent))+"  "+String.valueOf(reqPercent));
            nameTextView.setTextColor(Color.RED);
        }
        else
            nameTextView.setTextColor(Color.parseColor("#212121"));

        String tv2 = "P = "+String.valueOf(totslClassesAttended)+"   A = "+String.valueOf(totslClasses-totslClassesAttended)+"   Bunk = "
                + String.valueOf(calBunks(reqPercent,totslClassesAttended,totslClasses,Float.valueOf(percent)));
        detailTextView.setText(tv2);

        // implementing button's on click listener


        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(SubjectContract.SubjectEntry.COLUMN_CLSATND, totslClassesAttended+1);
                values.put(SubjectContract.SubjectEntry.COLUMN_TTLCLS, totslClasses+1);
                Integer rowsupdated = context.getContentResolver().update(SubjectContract.SubjectEntry.CONTENT_URI,values,"_id=?",new String[]{String.valueOf(view.getTag())});
                Log.i("rowsupdated", String.valueOf(rowsupdated));
                Toast.makeText(context, "Attendance Updated", Toast.LENGTH_SHORT).show();
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(SubjectContract.SubjectEntry.COLUMN_TTLCLS, totslClasses+1);
                Integer rowsupdated = context.getContentResolver().update(SubjectContract.SubjectEntry.CONTENT_URI,values,"_id=?",new String[]{String.valueOf(view.getTag())});
                notifyDataSetChanged();
                Log.i("rowsupdated", String.valueOf(rowsupdated));
                Toast.makeText(context, "Attendance Updated", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getpercent(float classesAttended, float totalClassses){
        if(classesAttended==0 && totalClassses==0)
            return String.valueOf(0.00);

        Float percent = ((classesAttended*100)/totalClassses);
        //   formting float vlaues to 2 decimal points
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(percent);
    }

    private int calBunks(float percentreq,float clsatnd,float totalcls, float percent){
        Log.i("percent", String.valueOf(percent));
        if(percentreq<percent)
        {
            int temp = (int) (100*clsatnd-percentreq*totalcls);
            return (int)(Math.floor(temp/percentreq));
        }
        else
            return 0;
    }




}
