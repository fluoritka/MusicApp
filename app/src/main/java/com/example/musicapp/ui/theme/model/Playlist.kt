package com.example.musicapp.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Класс плейлиста пользователя (RealmObject)
class Playlist : RealmObject {
    @PrimaryKey
    // Уникальный идентификатор плейлиста (UUID)
    var id: String = ""

    // ID владельца плейлиста
    var userId: String = ""

    // Название плейлиста
    var title: String = ""

    // Время создания в формате timestamp
    var createdAt: Long = 0L

    // Сохранённые треки в плейлисте
    var tracks: RealmList<SavedTrack> = realmListOf()
}