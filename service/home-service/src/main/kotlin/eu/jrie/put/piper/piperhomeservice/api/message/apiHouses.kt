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
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.IanaLinkRelations.CONTENTS
import org.springframework.hateoas.IanaLinkRelations.DESCRIBED_BY
import org.springframework.hateoas.IanaLinkRelations.DESCRIBES
import org.springframework.hateoas.IanaLinkRelations.RELATED
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo

private val linkToHouses = linkTo(HousesController::class.java)
private val linkToRooms = linkToHouses.slash("rooms")
private val linkToDevices = linkToHouses.slash("devices")
private val linkToDevicesTypes = linkToDevices.slash("types")

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
        val rooms: List<RoomMessage>
) : RepresentationalResponse(
        linkToRooms.withSelfRel(),
        linkToHouses.withRel(DESCRIBES)
)

data class RoomMessage (
        val id: String,
        val name: String
) : RepresentationalResponse(
        linkToHouses.slash("devices?roomId=$id").withRel(CONTENTS)
)

fun Room.asMessage() = RoomMessage(id, name)

data class RoomResponse (
        val id: String,
        val name: String,
        val devices: Set<DeviceResponse>
) : RepresentationalResponse(
        linkToRooms.slash(id).withSelfRel(),
        linkToRooms.withRel(COLLECTION),
)

@JvmName("asResponseRoomDevice")
suspend fun Pair<Room, Flow<Device>>.asResponse() = let { (room, devices) ->
    RoomResponse(room.id, room.name, devices.map { it.asMessage() }.toSet())
}

data class DeviceResponse (
        val id: String,
        val typeId: String,
        val name: String,
        val roomId: String
) : RepresentationalResponse(
        linkToDevices.slash(id).withSelfRel(),
        linkToDevices.withRel(COLLECTION),
        linkToHouses.slash("devices?roomId=$roomId").withRel(RELATED),
        linkToDevicesTypes.slash(typeId).withRel(DESCRIBED_BY)
)

data class DevicesResponse (
        val devices: List<DeviceResponse>,
        val params: Map<String, String>
) : RepresentationalResponse(
        linkToHouses.slash(params.asQuery()).withSelfRel(),
        linkToDevicesTypes.withRel(DESCRIBED_BY)
)

fun Device.asMessage() = DeviceResponse(id, typeId, name, roomId)

data class DeviceTypesResponse (
        val types: List<DeviceTypeResponse>
) : RepresentationalResponse(
        linkToDevicesTypes.withSelfRel(),
        linkToRooms.withRel(DESCRIBES)
)

data class DeviceTypeResponse (
        val id: String,
        val name: String,
        val events: Set<DeviceEventResponse>
) : RepresentationalResponse(
        linkToDevicesTypes.slash(id).withSelfRel(),
        linkToDevicesTypes.withRel(COLLECTION)
)

suspend fun Pair<DeviceType, Flow<DeviceEvent>>.asResponse() = let { (type, events) ->
    DeviceTypeResponse(type.id, type.name, events.map { it.asResponse() } .toSet())
}

data class DeviceEventResponse (
        val id: String,
        val deviceTypeId: String,
        val name: String
) : RepresentationalResponse(
        linkToDevices.slash("events").slash(id).withSelfRel(),
        linkToDevicesTypes.slash(deviceTypeId).withRel(DESCRIBED_BY)
)

fun DeviceEvent.asResponse() = DeviceEventResponse(id, deviceTypeId, name)
