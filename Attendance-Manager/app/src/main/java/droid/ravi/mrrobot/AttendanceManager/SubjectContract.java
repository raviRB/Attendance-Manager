package droid.ravi.mrrobot.AttendanceManager;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mr.Robot on 23-06-2018.
 */

public class SubjectContract {

    public static final String CONTENT_AUTHORITY = "droid.ravi.mrrobot.AttendanceManager";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SUBJECT = "subject";


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private SubjectContract() {}

    public static final class SubjectEntry implements BaseColumns {



        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUBJECT);

        /** Name of database table for subject */
        public final static String TABLE_NAME = "subject";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a subject pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;


        public final static String _ID = BaseColumns._ID;

        // COLUMN_TTLCLS ; Total classes of a subject - integer

        public final static String COLUMN_TTLCLS ="tclasses";

        // COLUMN_CLSATND ; Total classes attended of a subject - integer

        public final static String COLUMN_CLSATND ="classesattended";

        // COLUMN_SUB_NAME ; Name of a subject  - string

        public final static String COLUMN_SUB_NAME ="name";




    }

}
