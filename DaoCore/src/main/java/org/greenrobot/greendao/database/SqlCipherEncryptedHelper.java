package org.greenrobot.greendao.database;

import android.content.Context;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;

import java.util.Arrays;

class SqlCipherEncryptedHelper implements DatabaseOpenHelper.EncryptedHelper {

    private final DatabaseOpenHelper delegate;
    private final Context context;
    private final String name;
    private final int version;

    private SqlCipherOpenHelper sqLiteOpenHelper;

    public SqlCipherEncryptedHelper(DatabaseOpenHelper delegate, Context context, String name, int version, boolean loadLibs) {
        this.delegate = delegate;
        this.context = context;
        this.name = name;
        this.version = version;
        if (loadLibs) {
            System.loadLibrary("sqlcipher");
        }
    }

    private SQLiteOpenHelper getSQLiteOpenHelper(String password) {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new SqlCipherOpenHelper (
                    delegate, context, name, password, version
            );
        }
        return sqLiteOpenHelper;
    }

    @Override
    public Database getEncryptedReadableDb(String password) {
        return new EncryptedDatabase(getSQLiteOpenHelper(password).getReadableDatabase());
    }

    @Override
    public Database getEncryptedReadableDb(char[] password) {
        return new EncryptedDatabase(getSQLiteOpenHelper(Arrays.toString(password)).getReadableDatabase());
    }

    @Override
    public Database getEncryptedWritableDb(String password) {
        return new EncryptedDatabase(getSQLiteOpenHelper(password).getWritableDatabase());
    }

    @Override
    public Database getEncryptedWritableDb(char[] password) {
        return new EncryptedDatabase(getSQLiteOpenHelper(Arrays.toString(password)).getWritableDatabase());
    }
}
