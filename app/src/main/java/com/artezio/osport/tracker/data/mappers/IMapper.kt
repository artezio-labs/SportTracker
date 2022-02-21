package com.artezio.osport.tracker.data.mappers

interface IMapper<A, B> {
    fun map(obj: A): B
}