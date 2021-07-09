package top.warmthdawn.emss.features.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import top.warmthdawn.emss.features.server.dto.ServerInfoDTO

/**
 *
 * @author sunday7994
 * @date 2021/7/9
 */

fun Route.serverEndpoint() {

    val serverService by inject<ServerService>()
    route("/server"){
        get("/list") {
            call.respond(serverService.getServerInfo())
        }
        post("/create") {
            val dtoServerInfo = call.receive<ServerInfoDTO>()
            serverService.createServerInfo(dtoServerInfo)
            call.response.status(HttpStatusCode.OK)
        }
        post("/{id}/start") {
            val id = call.parameters["id"]!!.toLong()
            serverService.start(id)
            call.respond(true)
        }
        post("/{id}/stop") {
            val id = call.parameters["id"]!!.toLong()
            serverService.stop(id)
            call.respond(true)
        }
        post("/{id}/restart") {
            val id = call.parameters["id"]!!.toLong()
            serverService.restart(id)
            call.respond(true)
        }
        post("/{id}/terminate") {
            val id = call.parameters["id"]!!.toLong()
            serverService.terminate(id)
            call.respond(true)
        }
    }


}