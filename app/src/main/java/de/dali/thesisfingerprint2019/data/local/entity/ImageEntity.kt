package de.dali.thesisfingerprint2019.data.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey
    @NonNull
    var imageId: Long? = null,

    @ForeignKey(
        entity = FingerPrintEntity::class,
        parentColumns = ["imageId"],
        childColumns = ["fingerPrintId"],
        onDelete = ForeignKey.CASCADE
    )
    @NonNull
    var fingerPrintID: Long? = null,

    @NonNull
    var path: String? = null,

    @NonNull
    var biometricalID: Int? = null,

    @NonNull
    var timestamp: Long? = null,

    @NonNull
    var width: Int? = null,

    @NonNull
    var height: Int? = null,

    @NonNull
    var edgeDensity: Double? = null,

    @NonNull
    var correctionDegree: Double? = null
)