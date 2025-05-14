package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey

class FavoriteTrack : RealmObject {
    @PrimaryKey
    var id: String = ""          // trackId_userId
    var trackId: String = ""
    var userId:  String = ""

    var title:   String = ""
    var artist:  String = ""
    var imageUrl:String? = null

    @Index
    var addedAt: Long = 0L
}
