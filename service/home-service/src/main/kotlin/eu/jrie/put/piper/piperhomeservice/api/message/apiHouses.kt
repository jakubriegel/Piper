package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.domain.house.Consents
import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceType
import eu.jrie.put.piper.piperhomeservice.domain.house.House
import eu.jrie.put.piper.piperhomeservice.domain.house.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import org.springframework.hateoas.IanaLinkRelations.CANONICAL
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.IanaLinkRelations.DESCRIBED_BY
import org.springframework.hateoas.IanaLinkRelations.DESCRIBES
import org.springframework.hateoas.IanaLinkRelations.FIRST
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo

private val linkToHouses = linkTo(HousesController::class.java)
private val linkToRooms = linkToHouses.slash("rooms")
private val linkToDevicesTypes = linkToHouses.slash("devices").slash("types")

data class HouseResponse (
        val name: String,
        val consents: Consents
): RepresentationalResponse(
        linkToHouses.withSelfRel(),
        linkToHouses.withRel(COLLECTION),
        linkToRooms.withRel(DESCRIBED_BY)
)

fun House.asResponse() = HouseResponse(name, consents)

data class HouseCreatedResponse (
        val house: HouseResponse,
        val ownerUserLogin: String,
        val houseUseLogin: String
) : ApiResponse

data class HouseSchemaResponse (
    val deviceTypes: List<DeviceTypeResponse>,
    val rooms: List<RoomResponse>
) : RepresentationalResponse(
        linkToHouses.slash("schema").withSelfRel()
)

data class RoomsResponse (
        val rooms: List<RoomPreviewMessage>
) : RepresentationalResponse(
        linkToRooms.withSelfRel(),
        linkToRooms.slash(rooms.first().id).withRel(FIRST),
        linkToHouses.withRel(DESCRIBES)
)

data class RoomPreviewMessage (
        val id: String,
        val name: String
) : RepresentationalResponse(
        linkToRooms.slash(id).withRel(CANONICAL)
)

fun Room.asMessage() = RoomPreviewMessage(id, name)

data class RoomResponse (
        val id: String,
        val name: String,
        val devices: Set<DeviceMessage>
) : RepresentationalResponse(
        linkToRooms.slash(id).withSelfRel(),
        linkToRooms.withRel(COLLECTION),
)

@JvmName("asResponseRoomDevice")
suspend fun Pair<Room, Flow<Device>>.asResponse() = let { (room, devices) ->
    RoomResponse(room.id, room.name, devices.map { it.asMessage(room.id) }.toSet())
}

data class DeviceMessage (
        val id: String,
        val typeId: String,
        val name: String,
        private val roomId: String
) : RepresentationalResponse(
        linkToRooms.slash(roomId).withRel(COLLECTION),
        linkToDevicesTypes.slash(typeId).withRel(DESCRIBED_BY)
)

private fun Device.asMessage(roomId: String) = DeviceMessage(id, typeId, name, roomId)

data class DeviceTypesResponse (
        val types: List<DeviceTypeResponse>
) : RepresentationalResponse(
        linkToDevicesTypes.withSelfRel(),
        linkToDevicesTypes.slash(types.first().id).withRel(FIRST),
        linkToRooms.withRel(DESCRIBES)
)

data class DeviceTypeResponse (
        val id: String,
        val name: String,
        val events: Set<DeviceEventMessage>
) : RepresentationalResponse(
        linkToDevicesTypes.slash(id).withSelfRel(),
        linkToDevicesTypes.withRel(COLLECTION)
)

suspend fun Pair<DeviceType, Flow<DeviceEvent>>.asResponse() = let { (type, events) ->
    DeviceTypeResponse(type.id, type.name, events.map { DeviceEventMessage(it.id, it.name) } .toSet())
}

data class DeviceEventMessage (
        val id: String,
        val name: String
)
