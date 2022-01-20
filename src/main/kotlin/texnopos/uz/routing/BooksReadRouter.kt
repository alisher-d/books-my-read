package texnopos.uz.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import texnopos.uz.entities.ReadEntity
import texnopos.uz.entities.UserEntity
import texnopos.uz.models.GenericResponse
import texnopos.uz.models.ReadRequest
import texnopos.uz.models.ReadResponse

fun Route.booksReadRoutes(db: Database) {
    route("users/{id}/reads") {

        get {
            val id = call.parameters["id"]?.toInt() ?: -1
            if (!id.isExistUser(db)) {
                call.respond(
                    HttpStatusCode.BadRequest, GenericResponse(
                        success = false,
                        message = "User is not exist",
                        data = ""
                    )
                )
                return@get
            }
            val reads = db.from(UserEntity).crossJoin(ReadEntity).select()
                .where {
                    ReadEntity.readerId eq id
                }.map {
                    ReadResponse(
                        id = it[ReadEntity.id],
                        readerName = it[UserEntity.name],
                        readerSurname = it[UserEntity.surname],
                        bookId = it[ReadEntity.bookId],
                        conclusion = it[ReadEntity.conclusion],
                        createdAt = it[ReadEntity.createdAt],
                        updatedAt = it[ReadEntity.updatedAt],
                    )
                }
            if (reads.isEmpty()) {
                call.respond(
                    HttpStatusCode.NotFound, GenericResponse(
                        success = false,
                        message = "Books not found",
                        data = reads
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    GenericResponse(
                        success = true,
                        message = "${reads.size} books found",
                        data = reads
                    )
                )
            }
        }
        post {
            val id = call.parameters["id"]?.toInt() ?: -1
            val request = call.receive<ReadRequest>()
            if (!id.isExistUser(db)) {
                call.respond(
                    HttpStatusCode.BadRequest, GenericResponse(
                        success = false,
                        message = "User is not exist",
                        data = ""
                    )
                )
                return@post
            }
            if (!request.bookId.isExistBook(db)) {
                call.respond(
                    HttpStatusCode.BadRequest, GenericResponse(
                        success = false,
                        message = "This book is not exist",
                        data = ""
                    )
                )
                return@post
            }
            val result = db.insert(ReadEntity) {
                set(it.readerId, id)
                set(it.bookId, request.bookId)
                set(it.conclusion, request.conclusion)
                set(it.createdAt, System.currentTimeMillis())
                set(it.updatedAt, System.currentTimeMillis())
            }
            if (result == 1) {
                call.respond(
                    HttpStatusCode.OK, GenericResponse(
                        success = true,
                        message = "Read book has been successfully inserted",
                        data = ""
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, GenericResponse(
                        success = false,
                        message = "Failed to insert read book.",
                        data = ""
                    )
                )
            }
        }
    }
}