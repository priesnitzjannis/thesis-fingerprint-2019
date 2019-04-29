package de.dali.thesisfingerprint2019.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FingerPrintEntity(@PrimaryKey(autoGenerate = true) val id: Long)

