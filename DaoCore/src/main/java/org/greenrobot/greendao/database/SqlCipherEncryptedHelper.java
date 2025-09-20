package org.greenrobot.greendao.database;

import android.content.Context;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;

class SqlCipherEncryptedHelper implements DatabaseOpenHelper.EncryptedHelper {

    private final DatabaseOpenHelper delegate;
    private final Context context;
    private final String name;
    private final int version;
    private final boolean loadLibs;

    private SupportSQLiteOpenHelper supportSQLiteOpenHelper;

    public SqlCipherEncryptedHelper(DatabaseOpenHelper delegate, Context context, String name, int version, boolean loadLibs) {
        this.delegate = delegate;
        this.context = context;
        this.name = name;
        this.version = version;
        this.loadLibs = loadLibs;
        if (loadLibs) {
            System.loadLibrary("sqlcipher");
        }
    }

    private SupportSQLiteOpenHelper getSupportSQLiteOpenHelper(char[] password) {
        if (supportSQLiteOpenHelper == null) {
            SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
                    .name(name)
                    .callback(new SupportSQLiteOpenHelper.Callback(version) {
                        @Override
                        public void onCreate(SupportSQLiteDatabase db) {
                            delegate.onCreate(new EncryptedDatabase(db));
                        }

                        @Override
                        public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
                            delegate.onUpgrade(new EncryptedDatabase(db), oldVersion, newVersion);
                        }

                        @Override
                        public void onOpen(SupportSQLiteDatabase db) {
                            delegate.onOpen(new EncryptedDatabase(db));
                        }
                    })
                    .build();
            supportSQLiteOpenHelper = new net.zetetic.database.sqlcipher.SupportOpenHelperFactory(new String(password).getBytes()).create(configuration);
        }
        return supportSQLiteOpenHelper;
    }

    @Override
    public Database getEncryptedReadableDb(String password) {
        return new EncryptedDatabase(getSupportSQLiteOpenHelper(password.toCharArray()).getReadableDatabase());
    }

    @Override
    public Database getEncryptedReadableDb(char[] password) {
        return new EncryptedDatabase(getSupportSQLiteOpenHelper(password).getReadableDatabase());
    }

    @Override
    public Database getEncryptedWritableDb(String password) {
        return new EncryptedDatabase(getSupportSQLiteOpenHelper(password.toCharArray()).getWritableDatabase());
    }

    @Override
    public Database getEncryptedWritableDb(char[] password) {
        return new EncryptedDatabase(getSupportSQLiteOpenHelper(password).getWritableDatabase());
    }
}
