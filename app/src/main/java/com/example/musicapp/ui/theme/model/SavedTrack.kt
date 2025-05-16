package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Модель для сохранённых треков (история и избранное)
class SavedTrack : RealmObject {
    @PrimaryKey
    // Уникальный идентификатор записи
    var id: String = ""

    var trackUserId: String = ""
    var title: String? = null
    var artist: String? = null
    var imageUrl: String? = null

    var userId: String = ""

    // Время последнего воспроизведения (timestamp)
    var playedAt: Long = 0L

    // URL для стриминга трека
    var streamUrl: String? = null

    var playlistId: String? = null
}
