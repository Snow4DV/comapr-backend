package ru.snowadv.comaprbackend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.User
import ru.snowadv.comaprbackend.entity.roadmap.Category
import ru.snowadv.comaprbackend.entity.roadmap.Node
import ru.snowadv.comaprbackend.entity.roadmap.RoadMap
import ru.snowadv.comaprbackend.entity.roadmap.Task
import ru.snowadv.comaprbackend.repository.NodeRepository
import ru.snowadv.comaprbackend.repository.RoadMapRepository
import ru.snowadv.comaprbackend.repository.TaskRepository


@Service
class RoadMapService(
    private val mapRepo: RoadMapRepository, private val nodeRepo: NodeRepository, private val taskRepo: TaskRepository
) {

    fun getRoadMapById(id: Long): RoadMap? {
        return mapRepo.findByIdOrNull(id)
    }

    fun getTaskById(id: Long): Task? {
        return taskRepo.findByIdOrNull(id)
    }

    fun getNodeById(id: Long): Node? {
        return nodeRepo.findByIdOrNull(id)
    }

    fun getCreatorForMapId(id: Long): User {
        return getRoadMapById(id)?.creator ?: error("No map with id $id")
    }

    fun getCreatorForNodeId(id: Long): User {
        return getNodeById(id)?.creator ?: error("No node with id $id")
    }

    fun getCreatorForTaskId(id: Long): User {
        return getTaskById(id)?.creator ?: error("No task with id $id")
    }

    fun getStatusFormapId(id: Long): RoadMap.VerificationStatus {
        return getRoadMapById(id)?.status ?: error("No map with id $id")
    }

    fun addTask(nodeId: Long, task: Task) {
        val node = nodeRepo.findByIdOrNull(nodeId) ?: throw NoSuchElementException("Node with id $nodeId doesn't exist")

        node.tasks.add(task)
        nodeRepo.save(node)
    }

    fun removeTask(nodeId: Long, taskId: Long) {
        val node = nodeRepo.findByIdOrNull(nodeId)
        val task = taskRepo.findByIdOrNull(taskId)
        if (node == null || task == null) throw NoSuchElementException("Such ${if (node == null) "node" else "task"} doesn't exist!")

        node.tasks.remove(task)
        nodeRepo.save(node)
        taskRepo.delete(task)
    }

    fun addNode(mapId: Long, node: Node) {
        mapRepo.findByIdOrNull(mapId)?.let {
            it.nodes.add(node)
            mapRepo.save(it)
        } ?: throw NoSuchElementException("Map with id $mapId doesn't exist")
    }

    fun removeNode(mapId: Long, nodeId: Long) {
        val map = mapRepo.findByIdOrNull(mapId)
        val node = nodeRepo.findByIdOrNull(nodeId)
        if (map == null || node == null) throw NoSuchElementException("Such ${if (map == null) "map" else "node"} doesn't exist!")

        map.nodes.remove(node)
        mapRepo.save(map)
        nodeRepo.delete(node)
    }

    fun update(map: RoadMap) {
        if (mapRepo.findByIdOrNull(map.id ?: -1L) == null) {
            throw NoSuchElementException("Such map doesn't exist (with id ${map.id})")
        }

        mapRepo.save(map)
    }

    fun updateKeepCreatorAndStatus(map: RoadMap): Boolean {
        val oldMap = mapRepo.findByIdOrNull(map.id ?: -1L)
            ?: throw NoSuchElementException("Such map doesn't exist (with id ${map.id})")
        if (map.status != oldMap.status || map.creator.id != oldMap.creator.id) throw IllegalArgumentException("map has different status and/or creator")

        if (mapRepo.findByName(map.name) != null) {
            return false
        }

        mapRepo.save(map)
        return true
    }


    fun createNew(map: RoadMap): Boolean {
        if (mapRepo.findByIdOrNull(map.id ?: -1L) != null) {
            return false
        }
        if (mapRepo.findByName(map.name) != null) {
            throw NoSuchElementException("Such map already exists (with id ${map.id})")
        }

        mapRepo.save(map)
        return true
    }

    fun delete(mapId: Long) {
        mapRepo.deleteById(mapId)
    }


    fun getRoadMapsWithStatusAndOrCategory(statusId: Int?, categoryId: Long?): List<RoadMap> {
        val status = RoadMap.VerificationStatus.entries.firstOrNull { it.id == statusId }
        return getRoadMapsWithStatusAndOrCategory(status, categoryId)
    }

    fun getRoadMapsWithStatusAndOrCategory(status: RoadMap.VerificationStatus?, categoryId: Long?): List<RoadMap> {

        return when {
            status != null && categoryId != null -> mapRepo.findAllByStatusIsAndCategoryIdOrderByName(
                status, categoryId
            )

            status != null -> mapRepo.findAllByStatusIsOrderByName(status)
            categoryId != null -> mapRepo.findAllByCategoryIdOrderByName(categoryId)
            else -> emptyList()
        }
    }


}