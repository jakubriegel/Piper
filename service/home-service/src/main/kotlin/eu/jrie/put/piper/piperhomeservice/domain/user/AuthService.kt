package eu.jrie.put.piper.piperhomeservice.domain.user

import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceType
import eu.jrie.put.piper.piperhomeservice.domain.house.Room
import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import org.springframework.stereotype.Service

@Service
class AuthService {
    fun checkForRoutineAccess(user: User, routine: Routine) = routine.apply {
        if (user.house != routine.houseId) {
            throw InsufficientAccessException(resource = "routine", routine.id)
        }
    }
    fun checkForRoomAccess(user: User, room: Room) = room.apply {
        if (user.house != room.houseId) {
            throw InsufficientAccessException(resource = "room", room.id)
        }
    }
    fun checkForDeviceTypeAccess(user: User, type: DeviceType) = type.apply {
        if (user.house != type.houseId) {
            throw InsufficientAccessException(resource = "device type", type.id)
        }
    }
}
