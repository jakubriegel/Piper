package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.HOUSE_ID
import eu.jrie.put.piper.piperhomeservice.ROUTINE_ID
import eu.jrie.put.piper.piperhomeservice.USER
import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

internal class RoutinesServiceTest {

    private val repository: RoutinesRepository = mockk()
    private val authService: AuthService = mockk()

    private val service = RoutinesService(repository, authService)

    @Test
    fun `should delete routine`() {
        // given
        val routine = Routine(ROUTINE_ID, "name", HOUSE_ID, true, emptyList(), null)

        // and
        every { repository.findById(ROUTINE_ID) } returns just(routine)
        every { authService.checkForRoutineAccess(USER, routine) } returns routine
        every { repository.deleteById(ROUTINE_ID) } returns empty()

        // when
        service.deleteRoutine(ROUTINE_ID, USER).block()

        // then
        verifyOrder {
            repository.findById(ROUTINE_ID)
            authService.checkForRoutineAccess(USER, routine)
            repository.deleteById(ROUTINE_ID)
        }
    }

    @Test
    fun `should enable routine`() {
        // given
        val routine = Routine(ROUTINE_ID, "name", HOUSE_ID, false, emptyList(), null)
        val enabled = Routine(ROUTINE_ID, "name", HOUSE_ID, true, emptyList(), null)

        // and
        every { repository.findById(ROUTINE_ID) } returns just(routine)
        every { authService.checkForRoutineAccess(USER, routine) } returns routine
        every { repository.save(enabled) } returns empty()

        // when
        service.enableRoutine(ROUTINE_ID, USER).block()

        // then
        verifyOrder {
            repository.findById(ROUTINE_ID)
            authService.checkForRoutineAccess(USER, routine)
            repository.save(enabled)
        }
    }

    @Test
    fun `should disable routine`() {
        // given
        val routine = Routine(ROUTINE_ID, "name", HOUSE_ID, true, emptyList(), null)
        val disabled = Routine(ROUTINE_ID, "name", HOUSE_ID, false, emptyList(), null)

        // and
        every { repository.findById(ROUTINE_ID) } returns just(routine)
        every { authService.checkForRoutineAccess(USER, routine) } returns routine
        every { repository.save(disabled) } returns empty()

        // when
        service.disableRoutine(ROUTINE_ID, USER).block()

        // then
        verifyOrder {
            repository.findById(ROUTINE_ID)
            authService.checkForRoutineAccess(USER, routine)
            repository.save(disabled)
        }
    }
}
