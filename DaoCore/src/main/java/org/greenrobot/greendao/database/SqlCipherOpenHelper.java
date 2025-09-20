package org.greenrobot.greendao.database;

import android.content.Context;

import net.zetetic.database.DatabaseErrorHandler;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;

class SqlCipherOpenHelper extends SQLiteOpenHelper implements DatabaseOpenHelper.EncryptedHelper {

    private final DatabaseOpenHelper delegate;

    public SqlCipherOpenHelper(DatabaseOpenHelper delegate, Context context, String name, String password, int version) {
        super(
                context,
                name,
                password,
                null, version,
                0,
                null,
                null,
                false
        );
        this.delegate = delegate;
    }

    private Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new EncryptedDatabase(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        delegate.onCreate(wrap(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        delegate.onUpgrade(wrap(db), oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        delegate.onOpen(wrap(db));
    }

    @Override
    public Database getEncryptedReadableDb(String password) {
        return wrap(getReadableDatabase());
    }

    @Override
    public Database getEncryptedReadableDb(char[] password) {
        return wrap(getReadableDatabase());
    }

    @Override
    public Database getEncryptedWritableDb(String password) {
        return wrap(getWritableDatabase());
    }

    @Override
    public Database getEncryptedWritableDb(char[] password) {
        return wrap(getWritableDatabase());
    }

}

