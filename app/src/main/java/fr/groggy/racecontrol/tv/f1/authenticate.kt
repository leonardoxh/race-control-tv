package fr.groggy.racecontrol.tv.f1

import com.auth0.android.jwt.JWT
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class F1AuthenticateRequest(
    @Json(name = "Login") val login: String,
    @Json(name = "Password") val password: String)

@JsonClass(generateAdapter = true)
data class F1AuthenticateResponse(val data: Data) {
    companion object {
        @JsonClass(generateAdapter = true)
        data class Data(val subscriptionToken: String)
    }
}

data class F1Credentials(
    val login: String,
    val password: String
)

class F1Token(val value: JWT)
