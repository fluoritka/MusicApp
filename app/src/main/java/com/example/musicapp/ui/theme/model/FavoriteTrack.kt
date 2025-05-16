package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey

// RealmObject для хранения избранных треков пользователя
class FavoriteTrack : RealmObject {
    @PrimaryKey
    // Уникальный ключ: сочетание trackId и userId
    var id: String = ""

    var trackId: String = ""
    var userId:  String = ""

    var title:   String = ""
    var artist:  String = ""
    var imageUrl:String? = null

    @Index
    // Время добавления трека в избранное (timestamp)
    var addedAt: Long = 0L
}
