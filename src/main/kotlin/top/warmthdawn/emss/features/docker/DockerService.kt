package top.warmthdawn.emss.features.docker

import com.github.dockerjava.api.model.*
import io.ebean.Database
import kotlinx.coroutines.suspendCancellableCoroutine
import org.slf4j.LoggerFactory
import top.warmthdawn.emss.database.entity.Server
import top.warmthdawn.emss.database.entity.query.QImage
import top.warmthdawn.emss.database.entity.query.QServer
import top.warmthdawn.emss.features.file.FileService
import top.warmthdawn.emss.features.server.ServerException
import top.warmthdawn.emss.features.server.ServerExceptionMsg
import kotlin.coroutines.resume

/**
 *
 * @author WarmthDawn
 * @since 2021-07-17
 */
class DockerService(
    private val db: Database,
    private val fileService: FileService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DockerService::class.java)
    }
    fun init() {
        try {
            DockerManager.ping()
        }catch (e: Exception) {
            logger.error("无法连接docker， 请检查你的docker并尝试重启服务器")
        }
    }
    fun createContainer(serverId: Long) {
        val server = QServer(db).id.eq(serverId).findOne()!!
        val rootPath = fileService.processPath("/root/${server.location}").toString()
        val image = QImage(db).id.eq(server.imageId).findOne()!!
        val portBindingList =
            server.portBindings.map { PortBinding(Ports.Binding(null, it.key.toString()), ExposedPort(it.value)) }
        val volumeBindList = server.volumeBind.map { Bind(it.key, Volume(it.value)) } +
                Bind(rootPath, Volume(server.workingDir))
        val cmd = mutableListOf("/bin/sh", "-c", server.startCommand)
        val exposedPort = server.portBindings.keys.map { ExposedPort(it) }
        val containerId = DockerManager
            .createContainer(
                server.containerName,
                image.imageName,
                portBindingList,
                volumeBindList,
                server.workingDir,
                cmd,
                exposedPort
            )
        server.containerId = containerId
        server.update()
    }

    fun getContainerId(serverId: Long): String {
        val server = QServer(db).id.eq(serverId).findOne()!!
        //inspect一次，判断容器是否存在
        DockerManager.inspectContainer(server.containerId)
        return server.containerId!!
    }

    fun startContainer(serverId: Long) {
        DockerManager.startContainer(getContainerId(serverId))
    }
    fun startContainer(containerId: String) {
        DockerManager.startContainer(containerId)
    }

    fun stopContainer(serverId: Long) {
        DockerManager.stopContainer(getContainerId(serverId))
    }

    fun tryRemoveContainer(serverId: Long) {

        val server = QServer(db).id.eq(serverId).findOne()!!
        try {
            val status = DockerManager.inspectContainer(server.containerId)
            if(status.status == ContainerStatus.Running) {
                throw ServerException(ServerExceptionMsg.SERVER_NOT_STOPPED)
            }
            DockerManager.removeContainer(getContainerId(serverId))
        }catch (e: ContainerException) {
            if(e.containerExceptionMsg != ContainerExceptionMsg.CONTAINER_NOT_FOUND) {
                throw e
            }
        }
    }

    fun terminateContainer(serverId: Long) {
        DockerManager.terminateContainer(getContainerId(serverId))
    }

    fun isRunning(serverId: Long): Boolean {
        return inspectContainer(serverId) == ContainerStatus.Running
    }

    suspend fun waitContainer(serverId: Long) {
        suspendCancellableCoroutine<Unit> {
            DockerManager.waitContainer(getContainerId(serverId))
            it.resume(Unit)
            it.invokeOnCancellation {
                stopContainer(serverId)
            }
        }
    }

    fun inspectContainer(serverId: Long): ContainerStatus {
        val server = QServer(db).id.eq(serverId).findOne()!!
        return kotlin.runCatching {
            DockerManager.inspectContainer(server.containerId).status
        }.getOrDefault(ContainerStatus.Removed)
    }


}


val Server.containerName get() = "emss_container_${this.abbr}"