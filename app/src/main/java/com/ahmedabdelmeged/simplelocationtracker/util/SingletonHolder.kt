package com.ahmedabdelmeged.simplelocationtracker.util

/**
 * Provide a lazily and thread safe initialized singleton that take an argument.
 * Using a synchronized algorithm like the one used in kotlin [lazy].
 * That will produce the most efficient bytecode for the double-checked locking algorithm.
 * To use extends the companion object with this class and it will provide [getInstance]
 * e.g: With singleton that need a context for example like the database.
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }

}
