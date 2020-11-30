package eu.jrie.put.piper.piperhomeservice

import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import org.testcontainers.shaded.com.google.common.io.Files

internal val ROUTINE_ID = nextUUID
internal val HOUSE_ID = nextUUID
internal val DEVICE_ID = nextUUID
internal val EVENT_ID = nextUUID
internal val MODEL_ID = nextUUID
internal val USER = User(nextUUID, "login", "secret", HOUSE_ID, emptySet())

internal val TEMP_DIR = Files.createTempDir()
        .also { println("Temp dir for tests: ${it.absolutePath}") }
