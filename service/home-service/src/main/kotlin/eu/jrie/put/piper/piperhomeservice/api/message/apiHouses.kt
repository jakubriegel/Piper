package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.domain.house.Consents
import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceType
import eu.jrie.put.piper.piperhomeservice.domain.house.Room
import org.springframework.hateoas.IanaLinkRelations.CANONICAL
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.IanaLinkRelations.DESCRIBED_BY
import org.springframework.hateoas.IanaLinkRelations.DESCRIBES
import org.springframework.hateoas.IanaLinkRelations.FIRST
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo

private val linkToHouses = linkTo(HousesController::class.java)
private val linkToRooms = linkTo(HousesController::class.java).slash("rooms")

data class HouseResponse (
        val name: String,
        val consents: Consents
): RepresentationalResponse(
        linkToHouses.withSelfRel(),
        linkToHouses.withRel(COLLECTION),
        linkToRooms.withRel(DESCRIBED_BY)
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
        val devices: List<DeviceMessage>
) : RepresentationalResponse(
        linkToRooms.slash(id).withSelfRel(),
        linkToRooms.withRel(COLLECTION),
        linkToHouses.withRel(DESCRIBES),
        linkToHouses.slash("types").withRel(DESCRIBED_BY)
)

data class DeviceMessage (
        val id: String,
        val typeId: String,
        val name: String,
) : ApiMessage

fun List<Device>.asMessage() = map { it.asMessage() }
fun Device.asMessage() = DeviceMessage(id, typeId, name)

data class DeviceTypesResponse (
        val types: List<DeviceTypeResponse>
) : RepresentationalResponse(
        linkToHouses.slash("types").withSelfRel(),
        linkToHouses.slash("types").slash(types.first().id).withRel(FIRST),
        linkToRooms.withRel(DESCRIBES)
)

data class DeviceTypeResponse (
        val id: String,
        val name: String,
        val events: Set<DeviceEvent>
) : RepresentationalResponse(
        linkToHouses.slash("types").slash(id).withSelfRel(),
        linkToHouses.slash("types").withRel(COLLECTION)
)

fun List<DeviceType>.asResponse() = map { it.asResponse() }
fun DeviceType.asResponse() = DeviceTypeResponse(id, name, events)
