package app.android.simpleplayer.db;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyContentProvider extends ContentProvider
{

    public static final String AUTHORITY = "xiaojianyang.authority";

    public static final String DATABASE_NAME = "douqiuyun.db";

    public final static String tableName = "musicitem";

    final static int VERSION = 12;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/musicitem");

    SQLiteDatabase db;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        return db.delete(tableName, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        db.insert(tableName, null, values);
        return null;
    }

    @Override
    public boolean onCreate()
    {
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        return db.query(tableName, projection, selection, selectionArgs, null,
                null, sortOrder);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        db.update(tableName, values, selection, selectionArgs);
        return 0;
    }

}

class DBHelper extends SQLiteOpenHelper
{
    private static final String DB_CREATE = "CREATE TABLE "
            + MyContentProvider.tableName + "(" + "_id INTEGER PRIMARY KEY , "
            + "path TEXT, " + "name TEXT, " + "progress TEXT, "
            + "duration TEXT, " + "rank TEXT, " + "playing TEXT, "
            + "repeated TEXT, " + "visible TEXT, " + "hight TEXT);";

    public DBHelper(Context context)
    {
        super(context, MyContentProvider.DATABASE_NAME, null,
                MyContentProvider.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // System.out.println("db.execSQL(DB_CREATE);");
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS contactstable");
        onCreate(db);
    }
}