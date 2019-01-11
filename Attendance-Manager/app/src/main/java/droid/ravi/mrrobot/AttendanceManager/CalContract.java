package droid.ravi.mrrobot.AttendanceManager;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mr.Robot on 06-01-2019.
 */

public class CalContract {

    public static final String CONTENT_AUTHORITY = "droid.ravi.mrrobot.AttendanceManager.cal";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CAL = "Event";


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CalContract() {}

    public static final class CalEntry implements BaseColumns {



        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CAL);

        /** Name of database table for subject */
        public final static String TABLE_NAME = "Event";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAL;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a subject pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAL;

        // COLUMN_CLSATND ; Total classes attended of a subject - integer

        public final static String COLUMN_DAY ="day";

        // COLUMN_SUB_NAME ; Name of a subject  - string

        public final static String COLUMN_NOTE ="note";




    }


}
