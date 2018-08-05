package net.sarasarasa.lifeup.datas

import java.util.*

data class ToDo(var name: String,
                var content: String,
                var isChecked: Boolean,
                var ddl: Date?,
                var attr1: String?,
                var attr2: String?,
                var attr3: String?,
                var urgenceLevel: Int?,
                var difficultyLevel: Int?) {
}