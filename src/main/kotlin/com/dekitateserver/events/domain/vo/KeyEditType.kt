package com.dekitateserver.events.domain.vo

enum class KeyEditType(
        val id: String,
        val description: String
) {
    ITEM("item", "手のアイテムを鍵に設定"),
    TAKE("take", "[on/off] 使用した鍵を消去"),
    EXPIRE("expire", "[minutes] 鍵の有効期限"),
    BLOCK_MATERIAL("block-material", "[material] 設置ブロック素材"),
    BLOCK_LOCATION("block-location", "[here/x y z] 設置ブロック位置"),
    MATCH_MESSAGE("match-msg", "[msg] 一致時に表示"),
    NOT_MATCH_MESSAGE("not-match-msg", "[msg] 不一致時に表示"),
    EXPIRED_MESSAGE("expired-msg", "[msg] 期限切れ時に表示"),
    SHORT_AMOUNT_MESSAGE("short-amount-msg", "[msg] 数が足りないときに表示");

    companion object {
        fun find(id: String) = values().find { it.id == id }

        val HELP_MESSAGES = mutableListOf("--------- KeyEditType ---------").apply {
            ParkourEditType.values().forEach {
                add("| §9${it.id} ${it.description}")
            }

            add("| §7必須: [], 任意: <>")
            add("---------------------------------------")
        }.toTypedArray()
    }
}
