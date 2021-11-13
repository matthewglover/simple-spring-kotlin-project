package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = GET
        url = url("/greet")
    }
    response {
        status = OK
        body = body("message" to "Hello, world!")
        headers {
            contentType = "application/json"
        }
    }
}