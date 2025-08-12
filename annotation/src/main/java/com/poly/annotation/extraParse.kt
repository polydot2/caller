package com.poly.annotation

sealed class ExtraData {
    abstract val key: String
    abstract val required: Boolean
    abstract val init: ExtraInitMode

    data class StringExtra(
        override val key: String,
        override val required: Boolean,
        override val init: ExtraInitMode,
        val default: String
    ) : ExtraData()

    data class IntExtra(
        override val key: String,
        override val required: Boolean,
        override val init: ExtraInitMode,
        val default: Int
    ) : ExtraData()

    data class BooleanExtra(
        override val key: String,
        override val required: Boolean,
        override val init: ExtraInitMode,
        val default: Boolean
    ) : ExtraData()

    data class StringArrayExtra(
        override val key: String,
        override val required: Boolean,
        override val init: ExtraInitMode,
        val default: List<String>
    ) : ExtraData()
}