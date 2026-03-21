package dev.aurakai.auraframefx.api.client.apis

import dev.aurakai.auraframefx.api.client.infrastructure.ApiClient
import dev.aurakai.auraframefx.api.client.infrastructure.ApiResponse
import dev.aurakai.auraframefx.api.client.infrastructure.ClientError
import dev.aurakai.auraframefx.api.client.infrastructure.ClientException
import dev.aurakai.auraframefx.api.client.infrastructure.MultiValueMap
import dev.aurakai.auraframefx.api.client.infrastructure.RequestConfig
import dev.aurakai.auraframefx.api.client.infrastructure.RequestMethod
import dev.aurakai.auraframefx.api.client.infrastructure.ResponseType
import dev.aurakai.auraframefx.api.client.infrastructure.ServerError
import dev.aurakai.auraframefx.api.client.infrastructure.ServerException
import dev.aurakai.auraframefx.api.client.infrastructure.Success
import dev.aurakai.auraframefx.api.client.models.ConferenceRoom
import dev.aurakai.auraframefx.api.client.models.ConferenceRoomCreateRequest
import okhttp3.Call
import okhttp3.HttpUrl
import java.io.IOException

class ConferenceApi(
    basePath: kotlin.String = defaultBasePath,
    client: Call.Factory = ApiClient.defaultClient,
) : ApiClient(basePath, client) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties()
                .getProperty(ApiClient.baseUrlKey, "https://api.auraframefx.com/v1")
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(
        IllegalStateException::class,
        IOException::class,
        UnsupportedOperationException::class,
        ClientException::class,
        ServerException::class
    )
    fun conferenceRoomsPost(conferenceRoomCreateRequest: ConferenceRoomCreateRequest): ConferenceRoom {
        val localVarResponse =
            conferenceRoomsPostWithHttpInfo(conferenceRoomCreateRequest = conferenceRoomCreateRequest)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as ConferenceRoom
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException(
                    "Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}",
                    localVarError.statusCode,
                    localVarResponse
                )
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException(
                    "Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}",
                    localVarError.statusCode,
                    localVarResponse
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun conferenceRoomsPostWithHttpInfo(conferenceRoomCreateRequest: ConferenceRoomCreateRequest): ApiResponse<ConferenceRoom?> {
        val localVariableConfig =
            conferenceRoomsPostRequestConfig(conferenceRoomCreateRequest = conferenceRoomCreateRequest)
        return request<ConferenceRoomCreateRequest, ConferenceRoom>(localVariableConfig)
    }

    fun conferenceRoomsPostRequestConfig(conferenceRoomCreateRequest: ConferenceRoomCreateRequest): RequestConfig<ConferenceRoomCreateRequest> {
        val localVariableBody = conferenceRoomCreateRequest
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.POST,
            path = "/conference/rooms",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    private fun encodeURIComponent(uriComponent: kotlin.String): kotlin.String =
        HttpUrl.Builder().scheme("http").host("localhost").addPathSegment(uriComponent)
            .build().encodedPathSegments[0]
}
