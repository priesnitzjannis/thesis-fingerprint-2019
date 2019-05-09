package de.dali.thesisfingerprint2019.data.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "finger_print")
data class FingerPrintEntity(
    @PrimaryKey
    @NonNull
    var id: Long,

    @NonNull
    var gender: String,

    @NonNull
    var age: Int,

    @NonNull
    var fingerIndex: Int,

    @NonNull
    var skinColor: String,

    @NonNull
    var location: String,

    @NonNull
    var illumination: Int,

    @NonNull
    var resolution: String,

    @NonNull
    var correctionDegree: Float,

    @NonNull
    var vendor: String

) : Serializable

