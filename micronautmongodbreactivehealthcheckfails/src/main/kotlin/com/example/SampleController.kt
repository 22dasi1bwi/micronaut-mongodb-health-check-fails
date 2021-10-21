package com.example

import com.mongodb.reactivestreams.client.MongoClient
import io.micronaut.context.annotation.Context
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Context
@Controller
class SampleController(private val mongoClient: MongoClient) {

    @Get("/foo")
    fun get() = mongoClient.listDatabaseNames()
}