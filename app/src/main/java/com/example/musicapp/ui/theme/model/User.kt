package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Было class User, теперь:
class RealmUser : RealmObject {
    @PrimaryKey
    var id: String = ""
    var username: String = ""
    var password: String = ""
}
