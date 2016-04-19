package fw.musicauthors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by John on 18.04.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArtistsBD";
    public static final String TABLE_ARTISTS = "Artists";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_GENRES = "Genres";
    public static final String KEY_ALBUMS = "Albums";
    public static final String KEY_TRACKS = "Tracks";
    public static final String KEY_LINK = "Link";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_SMALLPIC = "Small_picture";
    public static final String KEY_BIGPIC = "Big_picture";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ARTISTS + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_GENRES + " text,"
                + KEY_ALBUMS + " integer," + KEY_TRACKS + " integer,"
                + KEY_LINK + " text," + KEY_DESCRIPTION + " text,"
                + KEY_SMALLPIC + " text," + KEY_BIGPIC + " text"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ARTISTS);

        onCreate(db);
    }
}
