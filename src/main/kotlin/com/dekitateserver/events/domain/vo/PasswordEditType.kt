package com.dekitateserver.events.domain.vo

enum class PasswordEditType(
        val id: String,
        val description: String
) {
    BLOCK_MATERIAL("block-material", "[material] 設置ブロック素材"),
    BLOCK_LOCATION("block-location", "[here/x y z] 設置ブロック位置"),
    MATCH_MESSAGE("match-msg", "[msg] 一致時に表示"),
    NOT_MATCH_MESSAGE("not-match-msg", "[msg] 不一致時に表示"),
    INPUT_MESSAGE("input-msg", "[msg] 入力時に表示, {buff}は入力値に置換"),
    RESET_MESSAGE("reset-msg", "[msg] 入力リセット時に表示");

    companion object {
        fun find(id: String) = values().find { it.id == id }
    }
}
