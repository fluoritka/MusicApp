package com.example.musicapp.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Playlist : RealmObject {
    @PrimaryKey
    var id: String = ""          // UUID
    var userId: String = ""
    var title:  String = ""
    var createdAt: Long = 0L

    /** Снимки треков на момент добавления */
    var tracks: RealmList<SavedTrack> = realmListOf()
}
