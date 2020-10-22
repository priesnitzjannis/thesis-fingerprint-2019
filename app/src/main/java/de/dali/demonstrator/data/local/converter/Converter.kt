package de.dali.demonstrator.data.local.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun listToCommaSeparated(value: List<String>?): String? = value?.joinToString(",")

    @TypeConverter
    fun commaSeparatedToList(value: String?): List<String>? = value?.split(",")?.map { it.trim() }
}