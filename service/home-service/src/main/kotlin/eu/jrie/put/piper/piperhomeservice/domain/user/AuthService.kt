package eu.jrie.put.piper.piperhomeservice.domain.user

import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import org.springframework.stereotype.Service

@Service
class AuthService {
    fun checkForHouseAccess(user: User, houseId: String) {
        if (user.house != houseId) {
            throw InsufficientAccessException(resource = "house")
        }
    }
    fun checkForRoutineAccess(user: User, routine: Routine) {
        if (user.house != routine.houseId) {
            throw InsufficientAccessException(resource = "routine", routine.id)
        }
    }
}
