package com.plktor

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(Koin) {
        modules(DatabaseModule)
    }

    val databaseAccessor: DatabaseAccessor by inject()


    routing {
        post("/adduser") {
            val request = call.receive<AddUserRequest>()
            databaseAccessor.insertUser(request)
            call.respondText("${request.name} + ${request.pet}", ContentType.Text.Plain)
        }

        get("/users") {
            call.respond(databaseAccessor.getUsersNames())
        }

        post("/userpet") {
            val request = call.receive<GetUserPetRequest>()
            val result = databaseAccessor.getUserPet(request.name)
            if (result.isNullOrEmpty()) {
                call.respondText("No pet =(", ContentType.Text.Plain)
            } else {
                call.respond(result)
            }
        }
    }
}