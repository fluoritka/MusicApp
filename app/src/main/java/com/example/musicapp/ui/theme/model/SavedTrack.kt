package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SavedTrack : RealmObject {
    @PrimaryKey
    var id: String = ""
    var trackUserId: String = ""
    var title: String? = ""
    var artist: String? = ""
    var imageUrl: String? = null
    var userId: String = ""
    var playedAt: Long = 0L

    // ────────────────────────────────────────────────────────────────
    // Новое поле для корректного воспроизведения из плейлистов:
    var streamUrl: String? = null
}
