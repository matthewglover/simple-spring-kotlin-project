package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.common.requestinput.Refineable
import javax.validation.constraints.NotBlank

data class RawNewUser(
    @get: NotBlank(message = "username required")
    val username: String?
) : Refineable<NewUser> {

    override fun unsafeRefine(): NewUser = NewUser(username = username!!)
}
