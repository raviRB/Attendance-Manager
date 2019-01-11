package droid.ravi.mrrobot.AttendanceManager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

;

public class CalendarAdapter extends ArrayAdapter<String> {

    private ArrayList<String> cellData;
    private ArrayList<Integer> markthese =null ;
    private LayoutInflater inflater;
    private int thisyear;
    private String thismonth;

    CalendarAdapter(@NonNull Context context, int resource, ArrayList<String> cells, int month, ArrayList<Integer> markthese, int year) {
        super(context,R.layout.grid_cell_layout ,cells);
        cellData = cells;
        this.markthese = markthese;
        inflater = LayoutInflater.from(context);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for(Integer d : markthese)
        thismonth = months[month];
        thisyear = year;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {


        String date = getItem(position);
        //Log.i("position index",String.valueOf(position));
        assert date != null;
        String[] s = date.split(" ");
        int day = Integer.parseInt(s[0]);
        String month = s[1];
        int year = Integer.parseInt(s[2]);

        if (view == null)
            view = inflater.inflate(R.layout.grid_cell_layout, parent, false);

        if(markthese.get(position)==1){
            view.setBackgroundResource(R.drawable.note);
        }

        if ( !month.equals(thismonth) || year != thisyear)
        {
            // if this day is outside current month, grey it out
            ((TextView)view).setTextColor(Color.parseColor("#808080"));
            //Log.i("from adapter ", String.valueOf(day));
        }
        else
        {
            // if it is today, set it to blue/bold
            //((TextView)view).setTypeface(null, Typeface.BOLD);
            ((TextView)view).setTextColor(Color.parseColor("#0099CC"));
            //Log.i(" adapter ", String.valueOf(day));
        }
        // if this day has an event, specify event image

        ((TextView)view).setText(String.valueOf(day));

        return view;
    }



}
