package de.dali.demonstrator.logging.SQLite;

import android.content.Context;
import android.os.Environment;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.dali.demonstrator.logging.SQLite.Dao.ImageDao;
import de.dali.demonstrator.logging.SQLite.Dao.ImageRunDao;
import de.dali.demonstrator.logging.SQLite.Dao.LoggingMessageDao;
import de.dali.demonstrator.logging.SQLite.Dao.ModuleDao;
import de.dali.demonstrator.logging.SQLite.Dao.PhoneDao;
import de.dali.demonstrator.logging.SQLite.Dao.AcquisitionDao;
import de.dali.demonstrator.logging.SQLite.Entity.Image;
import de.dali.demonstrator.logging.SQLite.Entity.ImageRun;
import de.dali.demonstrator.logging.SQLite.Entity.LoggingMessage;
import de.dali.demonstrator.logging.SQLite.Entity.Module;
import de.dali.demonstrator.logging.SQLite.Entity.Phone;
import de.dali.demonstrator.logging.SQLite.Entity.Acquisition;

@Database(entities = {
        Phone.class,
        LoggingMessage.class,
        Module.class,
        ImageRun.class,
        Image.class,
        Acquisition.class},
        exportSchema = false, version = 18)
public abstract class LoggingDatabase extends RoomDatabase {
    // TODO
    // Add Person, connected to run as expected Person or similar


    private static final String DB_Name = Environment.getExternalStorageDirectory() + "/thesis-fingerprints-2019/logging.db";
    private static LoggingDatabase instance;

    public static synchronized LoggingDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LoggingDatabase.class, DB_Name).fallbackToDestructiveMigration().allowMainThreadQueries().build();
            //instance = Room.databaseBuilder(context.getApplicationContext(), LoggingDatabase.class, DB_Name).build();
        }
        return instance;
    }

    public abstract PhoneDao phoneDao();

    public abstract LoggingMessageDao loggingMessageDao();

    public abstract ModuleDao moduleDao();

    public abstract ImageRunDao runDao();

    public abstract ImageDao imageDao();

    public abstract AcquisitionDao acquisitionDao();

}
