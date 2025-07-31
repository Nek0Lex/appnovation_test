package com.example.appnovation_test.model

data class BankInfo(
    val nameEn: String,
    val descEn: String,
    val actions: Action
)

data class Action(
    val recommended: List<ActionDetail>,
    val more: List<ActionDetail>
)

data class ActionDetail(
    val titleEn: String,
    val type: ActionType,
    val urlEn: String
) {
    companion object{
        fun fromString(type: String): ActionType {
            return when (type) {
                "link" -> ActionType.LINK
                "phone" -> ActionType.PHONE
                else -> ActionType.OTHERS
            }
        }
    }
}

enum class ActionType {
    LINK, PHONE, OTHERS
}