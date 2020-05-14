package de.dali.thesisfingerprint2019.logging.SQLite;

import android.content.Context;
import android.os.Environment;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.dali.thesisfingerprint2019.logging.SQLite.Dao.ImageDao;
import de.dali.thesisfingerprint2019.logging.SQLite.Dao.LoggingMessageDao;
import de.dali.thesisfingerprint2019.logging.SQLite.Dao.ModuleDao;
import de.dali.thesisfingerprint2019.logging.SQLite.Dao.PhoneDao;
import de.dali.thesisfingerprint2019.logging.SQLite.Dao.RunDao;
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Image;
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.LoggingMessage;
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Module;
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Phone;
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Run;

@Database(entities = {
        Phone.class,
        LoggingMessage.class,
        Module.class,
        Run.class,
        Image.class},
        exportSchema = false, version = 12)
public abstract class LoggingDatabase extends RoomDatabase {
    // TODO
    // Add Person, connected to run as expected Person or similar


    private static final String DB_Name = Environment.getExternalStorageDirectory() + "/thesis-fingerprints-2019/logging.db";
    private static LoggingDatabase instance;

    public static synchronized LoggingDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LoggingDatabase.class, DB_Name).fallbackToDestructiveMigration().build();
            //instance = Room.databaseBuilder(context.getApplicationContext(), LoggingDatabase.class, DB_Name).build();
        }
        return instance;
    }

    public abstract PhoneDao phoneDao();

    public abstract LoggingMessageDao loggingMessageDao();

    public abstract ModuleDao moduleDao();

    public abstract RunDao runDao();

    public abstract ImageDao imageDao();

}
