package com.matthewglover.simpleproject.common.logging

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

data class Tags(
    private val map: PersistentMap<String, String>
) : PersistentMap<String, String> by map {

    @Suppress("SpreadOperator")
    constructor(vararg pairs: Pair<String, String>) : this(persistentMapOf(*pairs))

    operator fun plus(other: Tags) = Tags(map.putAll(other))
}
