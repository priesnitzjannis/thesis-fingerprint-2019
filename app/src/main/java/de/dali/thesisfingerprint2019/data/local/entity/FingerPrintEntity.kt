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
    var personID: Long? = null,

    @NonNull
    var location: String? = null,

    @NonNull
    var illumination: Float? = null,

    @NonNull
    var resolution: String? = null,

    @NonNull
    var correctionDegree: Float? = null,

    @NonNull
    var vendor: String? = null,

    @NonNull
    var listOfFingerIds: List<String>? = null,

    @NonNull
    var imageList: List<String>? = null

) : Serializable

