package top.warmthdawn.emss.features.server

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import top.warmthdawn.emss.features.file.FileService
import top.warmthdawn.emss.features.permission.PermissionException
import top.warmthdawn.emss.features.server.dto.ServerInfoDTO
import top.warmthdawn.emss.utils.R
import top.warmthdawn.emss.utils.checkPermission
import top.warmthdawn.emss.utils.checkServerPermission

/**
 *
 * @author sunday7994
 * @date 2021/7/9
 */

fun Route.serverEndpoint() {

    val serverService by inject<ServerService>()
    val fileService by inject<FileService>()
    route("/servers") {
        get {
            R.ok(serverService.getServersBriefInfo())
        }

        post {
            checkPermission(0)
            val dtoServerInfo = call.receive<ServerInfoDTO>()
            serverService.createServerInfo(dtoServerInfo)
            R.ok()
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 2)
            }
            serverService.removeServer(id)
            R.ok()
        }
        get("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            R.ok(serverService.getServerInfo(id))
        }
        get("/{id}/filePath") {
            val id = call.parameters["id"]!!.toLong()
            R.ok(fileService.getServerPath(id))
        }

        post("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 2)
            }
            val dto = call.receive<ServerInfoDTO>()
            serverService.updateServerInfo(id, dto)
            R.ok()
        }
        post("/{id}/autoRestart") {
            val id = call.parameters["id"]!!.toLong()
            val value = call.request.queryParameters["value"].toBoolean()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 3)
            }
            serverService.setAutoRestart(id, value)
            R.ok()
        }
        post("/{id}/start") {
            val id = call.parameters["id"]!!.toLong()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 3)
            }
            serverService.start(id)
            R.ok()
        }
        post("/{id}/stop") {
            val id = call.parameters["id"]!!.toLong()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 3)
            }
            serverService.stop(id)
            R.ok()
        }

        post("/{id}/restart") {
            val id = call.parameters["id"]!!.toLong()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 3)
            }
            serverService.restart(id)
            R.ok()
        }
        post("/{id}/terminate") {
            val id = call.parameters["id"]!!.toLong()
            try {
                checkPermission(0)
            } catch (e: PermissionException) {
                checkServerPermission(id, 3)
            }
            serverService.terminate(id)
            R.ok()
        }


    }

}