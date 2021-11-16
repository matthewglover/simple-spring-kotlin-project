package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = POST
        url = url("/users")
        headers {
            contentType = "text/plain"
        }
        body = body(file("valid_new_user_payload.json"))
    }
    response {
        status = UNSUPPORTED_MEDIA_TYPE
        headers {
            contentType = "application/json"
        }
        body = body("errorType" to "UnsupportedMediaTypeStatusException")
        bodyMatchers {
            jsonPath("$.errors[0]", byRegex("Content type: `text/plain.*` not supported."))
        }
    }
}
