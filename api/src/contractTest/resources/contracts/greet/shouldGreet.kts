package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = GET
        url = url("/greet")
    }
    response {
        status = OK
        body = body(file("greeting.json"))
        headers {
            contentType = "application/json"
        }
    }
}
