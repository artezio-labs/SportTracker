package com.artezio.sporttracker.data.mappers

interface IMapper<A, B> {
    fun map(obj: A): B
}