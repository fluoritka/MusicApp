// app/src/main/java/com/example/musicapp/model/SavedTrack.kt
package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SavedTrack : RealmObject {
    @PrimaryKey
    var id: String = ""
    var trackUserId: String = ""
    var title: String? = null
    var artist: String? = null
    var imageUrl: String? = null
    var userId: String = ""
    var playedAt: Long = 0L

    // НОВОЕ поле для URL потока
    var streamUrl: String? = null

    // НОВОЕ поле связи с плейлистом
    var playlistId: String? = null
}
