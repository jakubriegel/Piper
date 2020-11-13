package eu.jrie.put.piper.piperhomeservice

import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import org.testcontainers.shaded.com.google.common.io.Files

internal val HOUSE_ID = nextUUID
internal val DEVICE_ID = nextUUID
internal val EVENT_ID = nextUUID

internal val TEMP_DIR = Files.createTempDir()
        .also { println("Temp dir for tests: ${it.absolutePath}") }
