package de.dali.thesisfingerprint2019.logging.SQLite.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.thesisfingerprint2019.logging.SQLite.Entity.LoggingMessage;

@Dao
public interface LoggingMessageDao {

    @Query("Select * from loggingMessage")
    List<LoggingMessage> getAll();

    @Query("Delete from loggingMessage")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLoggingMessage(LoggingMessage message);

    @Update
    void updateLoggingMessage(LoggingMessage message);

    @Delete
    void deleteLoggingMessage(LoggingMessage message);
}
