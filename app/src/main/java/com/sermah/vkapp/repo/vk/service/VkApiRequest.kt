package com.sermah.vkapp.repo.vk.service

import com.google.gson.JsonParser
import com.google.gson.internal.bind.JsonTreeReader
import com.google.gson.stream.JsonReader
import com.sermah.vkapp.repo.vk.model.UserIdExt
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

internal class VKApiRequest<T> internal constructor(
    methodName: String,
    private val parser: (JsonReader) -> T
) : VKRequest<T>(methodName, requestApiVersion = "5.131") {

    override fun parse(responseJson: JSONObject): T =
        parser(
            JsonTreeReader(
                JsonParser.parseString(responseJson.toString()).asJsonObject["response"]
            )
        )

    fun addParam(
        name: String,
        values: List<UserIdExt>,
        min: Long = Long.MIN_VALUE,
        max: Long = Long.MAX_VALUE
    ) {
        addParam(name, values.joinToString(",", transform = {
            when (it) {
                is UserIdExt.Id -> {
                    if (it.value !in min..max) {
                        throw IllegalArgumentException("Param $name not in $min..$max")
                    }
                    it.value.toString()
                }

                is UserIdExt.Name -> it.value
            }
        }
        ))
    }
}
