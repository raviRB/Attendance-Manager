package droid.ravi.mrrobot.AttendanceManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mr.Robot on 23-06-2018.
 */

public class AttendanceDbHelper extends SQLiteOpenHelper{
    public static final String LOG_TAG = AttendanceDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "Attendance.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link AttendanceDbHelper}.
     *
     * @param context of the app
     */
    public AttendanceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + SubjectContract.SubjectEntry.TABLE_NAME + " ("
                + SubjectContract.SubjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SubjectContract.SubjectEntry.COLUMN_SUB_NAME + " TEXT NOT NULL, "
                + SubjectContract.SubjectEntry.COLUMN_TTLCLS+ " INTEGER NOT NULL DEFAULT 0, "
                + SubjectContract.SubjectEntry.COLUMN_CLSATND + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
