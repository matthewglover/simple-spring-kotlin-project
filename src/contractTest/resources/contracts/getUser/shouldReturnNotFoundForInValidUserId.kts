package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = GET
        url = url("/users/invalid-user-id")
    }
    response {
        status = NOT_FOUND
    }
}
