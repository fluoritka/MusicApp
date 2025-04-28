// app/src/main/java/com/example/musicapp/model/SavedTrack.kt
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
    var userId: String = ""       // за каким пользователем закреплено прослушивание

    @Index
    var playedAt: Long = 0L       // время в millis
}
