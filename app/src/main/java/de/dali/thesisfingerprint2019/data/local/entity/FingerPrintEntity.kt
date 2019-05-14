package de.dali.thesisfingerprint2019.data.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "finger_print")
data class FingerPrintEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long = 0,

    @ForeignKey(
        entity = TestPersonEntity::class,
        parentColumns = ["id"],
        childColumns = ["personID"],
        onDelete = CASCADE
    )
    @NonNull
    var personID: Long,

    @NonNull
    var location: String,

    @NonNull
    var illumination: Float,

    @NonNull
    var resolution: String,

    @NonNull
    var correctionDegree: Float,

    @NonNull
    var vendor: String,

    @NonNull
    var listOfFingerIds: List<String>,

    @NonNull
    var imageList: List<String>

) : Serializable

