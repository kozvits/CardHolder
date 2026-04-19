// app/src/main/java/com/example/nfccardmanager/data/Card.kt
package com.example.nfccardmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val type: String,
    val data: ByteArray,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

        if (id != other.id) return false
        if (uid != other.uid) return false
        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + uid.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
