package com.matthewglover.simpleproject.common.logging

import com.matthewglover.simpleproject.utils.MockHandlerUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest

internal class RequestResponseLoggingTest {

    @Test
    internal fun `logs request and then response`() = runBlocking {
        val contextLogger = mockk<ContextLogger<RequestResponseLogging>>()
        val infoCalls = mutableListOf<TagProvider>()
        coEvery { contextLogger.info(capture(infoCalls)) } just Runs

        val contextLoggerFactory = mockk<ContextLoggerFactory>()
        every { contextLoggerFactory.create(any<Class<RequestResponseLogging>>()) } returns contextLogger

        val requestResponseLogging = RequestResponseLogging(contextLoggerFactory)

        val httpStatus = HttpStatus.OK
        val serverRequest = MockServerRequest.builder().build()

        requestResponseLogging.filter(serverRequest, MockHandlerUtils.mockNext(httpStatus))

        coVerify(exactly = 2) { contextLogger.info(any()) }
        confirmVerified(contextLogger)

        assertEquals(Tags("requestStatus" to "Starting"), infoCalls[0]())
        assertEquals(
            Tags(
                "requestStatus" to "Completed",
                "statusCode" to httpStatus.value().toString(),
                "reasonPhrase" to httpStatus.reasonPhrase
            ),
            infoCalls[1]()
        )
    }
}
