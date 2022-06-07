package com.artezio.osport.tracker.data.permissions.chain

abstract class Link {

    private var next: Link? = null

    fun linkWith(next: Link?): Link? {
        this.next = next
        return next
    }

    abstract fun check(): Boolean

    protected fun checkNext() {
        next?.check()
    }
}