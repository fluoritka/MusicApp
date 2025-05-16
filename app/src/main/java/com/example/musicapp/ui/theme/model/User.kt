package com.example.musicapp.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Realm модель для хранения данных пользователя
class RealmUser : RealmObject {
    @PrimaryKey
    // Уникальный идентификатор пользователя
    var id: String = ""
    var username: String = ""
    var password: String = ""
}