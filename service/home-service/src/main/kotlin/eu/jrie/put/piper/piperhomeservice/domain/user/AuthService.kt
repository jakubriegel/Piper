package eu.jrie.put.piper.piperhomeservice.domain.user

import org.springframework.stereotype.Service

@Service
class AuthService {
    fun checkForHouseAccess(user: User, houseId: String) {
        if (user.house != houseId) {
            throw InsufficientAccessException(resource = "house")
        }
    }
}
