package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = POST
        url = url("/users")
        headers {
            contentType = "application/json"
        }
        body = body(file("valid_new_user_payload.json"))
    }
    response {
        status = OK
        body = body(file("new_user.json"))
        headers {
            contentType = "application/json"
        }
    }
}
