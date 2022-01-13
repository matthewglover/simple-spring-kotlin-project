package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = POST
        url = url("/users")
        headers {
            contentType = "application/json"
        }
        body = body(file("invalid_new_user_payload.json"))
    }
    response {
        status = BAD_REQUEST
        headers {
            contentType = "application/json"
        }
        body = body(file("invalid_new_user_response.json"))
    }
}
