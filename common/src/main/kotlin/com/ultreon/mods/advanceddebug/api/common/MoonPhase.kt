package com.ultreon.mods.advanceddebug.api.common

enum class MoonPhase(override val index: Int) : IIndexable {
    FULL(0), WANING_GIBBOUS(1), THIRD_QUARTER(2), WANING_CRESCENT(3), NEW(4), WAXING_CRESCENT(5), FIRST_QUARTER(6), WAXING_GIBBOUS(
        7
    );

    companion object {
        fun fromIndex(index: Int): MoonPhase? {
            val values = values()
            for (value in values) {
                if (value.index == index) {
                    return value
                }
            }
            return null
        }
    }
}

val Int.asMoonPhase get() = MoonPhase.fromIndex(this)
