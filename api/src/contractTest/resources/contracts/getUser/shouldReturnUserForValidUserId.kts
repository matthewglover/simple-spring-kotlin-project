package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = GET
        url = url("/users/valid-user-id")
    }
    response {
        status = OK
        body = body(file("valid_user.json"))
        headers {
            contentType = "application/json"
        }
    }
}
