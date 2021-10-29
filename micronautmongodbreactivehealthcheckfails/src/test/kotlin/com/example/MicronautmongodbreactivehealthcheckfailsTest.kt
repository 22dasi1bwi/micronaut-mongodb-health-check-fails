package com.example

import com.mongodb.reactivestreams.client.MongoClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Flux
import java.util.*
import javax.inject.Inject

val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10").toString())

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautmongodbreactivehealthcheckfailsTest : TestPropertyProvider {

    @Inject
    lateinit var server: EmbeddedServer

    @Test
    fun `performing Health-Check fails`() {
        RestAssured.requestSpecification = RequestSpecBuilder().setBaseUri(server.uri.toString()).build()

        val findBean: Optional<MongoClient> = server.applicationContext.findBean(MongoClient::class.java)

        /** Actually initializes the client. Lets the health status of Mongo switch from `UNKNOWN` to `UP`. */
        Flux.from(findBean.get().listDatabaseNames()).map { println(it) }.subscribe()

        RestAssured.given().request()
            .get("/health")
            .then()
            .statusCode(200)
            .body(Matchers.equalTo("""{"status":"UP"}"""))
    }

    override fun getProperties(): Map<String, String> {
        if (!mongoDBContainer.isRunning) {
            mongoDBContainer.start()
        }
        return mapOf("MONGODB_URI" to mongoDBContainer.replicaSetUrl)
    }
}
