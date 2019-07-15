package de.dali.thesisfingerprint2019.data.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "test_person")
data class TestPersonEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var personID: Long? = null,

    @NonNull
    var name: String,

    @NonNull
    var gender: String,

    @NonNull
    var age: Int,

    @NonNull
    var skinColor: String,

    @NonNull
    var timestamp: Long

) : Serializable

