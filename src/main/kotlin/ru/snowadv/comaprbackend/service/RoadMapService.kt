package ru.snowadv.comaprbackend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.snowadv.comaprbackend.entity.roadmap.Node
import ru.snowadv.comaprbackend.entity.roadmap.RoadMap
import ru.snowadv.comaprbackend.entity.roadmap.Task
import ru.snowadv.comaprbackend.repository.NodeRepository
import ru.snowadv.comaprbackend.repository.RoadMapRepository
import ru.snowadv.comaprbackend.repository.TaskRepository


@Service
class RoadMapService(val repository: RoadMapRepository, val nodeRepository: NodeRepository,
                     val taskRepository: TaskRepository) {

    fun getRoadMapById(id: Long): RoadMap? {
        return repository.findByIdOrNull(id)
    }


    fun addTask(nodeId: Long, task: Task) {
        val node = nodeRepository.findByIdOrNull(nodeId) ?: throw NoSuchElementException("Node with id $nodeId doesn't exist")

        node.tasks.add(task)
        nodeRepository.save(node)
    }

    fun removeTask(nodeId: Long, taskId: Long) {
        val node = nodeRepository.findByIdOrNull(nodeId)
        val task = taskRepository.findByIdOrNull(taskId)
        if(node == null || task == null) throw NoSuchElementException("Such ${if (node == null) "node" else "task"} doesn't exist!")

        node.tasks.remove(task)
        nodeRepository.save(node)
        taskRepository.delete(task)
    }

    fun addNode(mapId: Long, node: Node) {
        repository.findByIdOrNull(mapId)?.let {
            it.nodes.add(node)
            repository.save(it)
        } ?: throw NoSuchElementException("Map with id $mapId doesn't exist")
    }

    fun removeNode(mapId: Long, nodeId: Long) {
        val map = repository.findByIdOrNull(mapId)
        val node = nodeRepository.findByIdOrNull(nodeId)
        if(map == null || node == null) throw NoSuchElementException("Such ${if (map == null) "map" else "node"} doesn't exist!")

        map.nodes.remove(node)
        repository.save(map)
        nodeRepository.delete(node)
    }

    fun update(map: RoadMap) {
        if(repository.findByIdOrNull(map.id ?: -1L) == null) {
            throw NoSuchElementException("Such map doesn't exist (with id ${map.id})")
        }

        repository.save(map)
    }


    fun createNew(map: RoadMap) {
        if(repository.findByIdOrNull(map.id ?: -1L) != null) {
            throw NoSuchElementException("Such map already exists (with id ${map.id})")
        }

        repository.save(map)
    }

    fun delete(mapId: Long) {
        repository.deleteById(mapId)
    }


}