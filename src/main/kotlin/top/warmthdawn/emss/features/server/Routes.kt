package top.warmthdawn.emss.features.server

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import top.warmthdawn.emss.features.dockerStats.StatsService
import top.warmthdawn.emss.features.server.dto.ServerInfoDTO
import top.warmthdawn.emss.utils.R

/**
 *
 * @author sunday7994
 * @date 2021/7/9
 */

fun Route.serverEndpoint() {

    val serverService by inject<ServerService>()
    val statsService by inject<StatsService>()
    route("/server"){
        get("/list") {
            R.ok(serverService.getServerInfo())
        }
        get("/{id}"){
            val id = call.parameters["id"]!!.toLong()
            R.ok(serverService.getServerInfo(id))
        }
        get("/{id}/stats"){
            val id = call.parameters["id"]!!.toLong()
            R.ok(statsService.serverStatsInfoMap[id]!!)
        }
        post("/create") {
            val dtoServerInfo = call.receive<ServerInfoDTO>()
            serverService.createServerInfo(dtoServerInfo)
            R.ok()
        }
        post("/{id}/start") {
            val id = call.parameters["id"]!!.toLong()
            serverService.start(id)
            R.ok()
        }
        post("/{id}/stop") {
            val id = call.parameters["id"]!!.toLong()
            serverService.stop(id)
            R.ok()
        }
        post("/{id}/restart") {
            val id = call.parameters["id"]!!.toLong()
            serverService.restart(id)
            R.ok()
        }
        post("/{id}/terminate") {
            val id = call.parameters["id"]!!.toLong()
            serverService.terminate(id)
            R.ok()
        }
        delete("/{id}"){
            val id = call.parameters["id"]!!.toLong()
            serverService.removeServer(id)
            R.ok()
        }


    }

}