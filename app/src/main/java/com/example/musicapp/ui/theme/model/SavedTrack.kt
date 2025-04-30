package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey

class SavedTrack : RealmObject {
    @PrimaryKey
    var id: String = ""           // id трека
    var title: String = ""
    var artist: String = ""
    var imageUrl: String? = null

    @Index
    var userId: String = ""       // id залогиненного пользователя

    @Index
    var playedAt: Long = 0L       // время в millis

    @Index
    var trackUserId: String = ""   // ID артиста/владельца трека (track.user.id)
}
