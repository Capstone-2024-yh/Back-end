package com.capstone.backend.Service

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.Base64

interface GptApiService {
    @POST("/v1/threads/runs")
    @Headers("Content-Type: application/json", "OpenAI-Beta: assistants=v2")
    fun getResponse(
        @Header("Authorization") authHeader: String,
        @Body request: MakeThreadDTO
    ): Call<ResponseTreadDTO>

    @POST("/v1/threads/runs")
    @Headers("Content-Type: application/json", "OpenAI-Beta: assistants=v2")
    fun getResponse(
        @Header("Authorization") authHeader: String,
        @Body request: IMakeThreadDTO
    ): Call<ResponseTreadDTO>

    @GET("/v1/threads/{thread_id}/messages")
    @Headers("Content-Type: application/json", "OpenAI-Beta: assistants=v2")
    fun getMessage(
        @Path("thread_id") threadId: String,
        @Header("Authorization") authHeader: String,
    ): Call<ThreadMessagesResponse>


    @GET("/v1/threads/{thread_id}/runs/{run_id}")
    @Headers("Content-Type: application/json", "OpenAI-Beta: assistants=v2")
    fun checkRun(
        @Path("thread_id") threadId: String,
        @Path("run_id") runId: String,
        @Header("Authorization") authHeader: String,
    ) : Call<CheckRunDTO>

    @Multipart
    @POST("vl/files")
    @Headers("Content-Type: application/json", "OpenAI-Beta: assistants=v2")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("purpose") purpose : RequestBody
    ) : Call<FileDTO>
}

@Service
class GptService {
    private final val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/") // GptAPI 서버 URL
        .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 Gson 컨버터
        .build()

    val apiService = retrofit.create(GptApiService::class.java)

    fun getResponse(str: String): String {
        return gptCall(str, System.getProperty("GPT_ASSISTANCE"))
    }

    fun getTokenToSearch(input : String) : TokenListDTO? {
        return getToken(input = input, System.getProperty("GPT_TOKEN"))
    }

    fun getTokenToVenue(input : String) : TokenListDTO? {
        return getToken(input = input, System.getProperty("GPT_VENUE_TOKEN"))
    }

    fun getFeedback(input : FeedbackDTO) : FeedbackResponseDTO? {
        return getJsonObject<FeedbackResponseDTO>(input.toString(), System.getProperty("GPT_FEEDBACK"))
    }

    private fun getToken(input : String, assistantId: String): TokenListDTO? {
        return try {
            val callResp = gptCall(input, assistantId)
            val res = callResp.replace("```json", "").replace("```", "").trim()
            println(res)
            val mapper = ObjectMapper().registerKotlinModule()
            mapper.readValue(res, TokenListDTO::class.java)
        }
        catch (e: Exception) {
            println(e)
            null
        }
    }

    private inline fun <reified T> getJsonObject(input : String, assistantId: String) : T? {
        return try {
            val callResp = gptCall(input, assistantId)
            val res = callResp.replace("```json", "").replace("```", "").trim()
            println(res)
            val mapper = ObjectMapper().registerKotlinModule()
            mapper.readValue(res, T::class.java)
        }
        catch (e: Exception) {
            println(e)
            null
        }
    }

    fun getImageTokenToVenue(base64: String) : ImageAnalysisResponse? {
        return getImageToken(base64, System.getProperty("GPT_IMAGE_TOKEN"))
    }

    fun getImageToken(base64 : String, assistantId: String) : ImageAnalysisResponse?{
        return try {
            val callResp = gptImageCall(base64, assistantId)
            val res = callResp.replace("```json", "").replace("```", "").trim()
            println(res)
            val mapper = ObjectMapper().registerKotlinModule()
            mapper.readValue(res, ImageAnalysisResponse::class.java)
        }
        catch (e: Exception) {
            println(e)
            null
        }
    }

    fun uploadImage(base64 : String, fileName: String) : FileDTO? {
        val purpose = "vision".toRequestBody("text/plain".toMediaTypeOrNull())

        val image = Base64.getDecoder().decode(base64)
        val requestFile = RequestBody.create("application/json".toMediaTypeOrNull(), image)
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

        val call = apiService.uploadFile(filePart, purpose).execute()

        return try {
            call.body()
        }
        catch (e : Exception){
            print(e)
            null
        }
    }

    private fun gptImageCall(base64 : String, assistantId: String) : String {
        return try{
            val gptAuth : String = System.getProperty("GPT_AUTH")

            // getResponse API 호출
            val threadRunResponse = apiService.getResponse(
                authHeader = "Bearer $gptAuth",
                IMakeThreadDTO(
                    assistant_id = assistantId,
                    thread = IThreadData(
                        messages = listOf(
                            IMessageDTO(
                                role = "user",
                                content = listOf(
                                    ImageDTO(
                                        type = "image_url",
                                        image_url = ImageURL(base64)
                                    )
                                )
                            )
                        )
                    )
                )
            ).execute() // 동기 호출

            if (threadRunResponse.isSuccessful) {
                val threadRun = threadRunResponse.body()
                val threadId = threadRun!!.thread_id
                getResponse(gptAuth, threadRun, threadId)
            } else {
                throw RuntimeException("Failed to get response: ${threadRunResponse.errorBody()?.string()}")
            }
        }
        catch (e : Exception){
            println(e)
            ""
        }
    }

    private fun gptCall(input : String, assistantId: String) : String{
        return try{
            val gptAuth : String = System.getProperty("GPT_AUTH")

            // getResponse API 호출
            val threadRunResponse = apiService.getResponse(
                authHeader = "Bearer $gptAuth",
                MakeThreadDTO(
                    assistant_id = assistantId,
                    thread = ThreadData(
                        messages = listOf(
                            MessageDTO(
                                role = "user",
                                content = input
                            )
                        )
                    )
                )
            ).execute() // 동기 호출

            if (threadRunResponse.isSuccessful) {
                val threadRun = threadRunResponse.body()
                val threadId = threadRun!!.thread_id
                getResponse(gptAuth, threadRun, threadId)
            } else {
                throw RuntimeException("Failed to get response: ${threadRunResponse.errorBody()?.string()}")
            }
        }
        catch (e : Exception){
            println(e)
            ""
        }
    }

    private fun getResponse(gptAuth : String, threadRun : ResponseTreadDTO, threadId: String) : String {
        val runId = threadRun.id

        // 최대 대기 시간 설정 (예: 1분)
        val maxWaitTimeMillis = 60_000
        val startTime = System.currentTimeMillis()

        // checkRun을 호출해 상태가 "Complete"가 될 때까지 대기
        var status: String
        do {
            val checkRunResponse = apiService.checkRun(
                threadId = threadId,
                runId = runId,
                authHeader = "Bearer $gptAuth"
            ).execute()

            if (checkRunResponse.isSuccessful) {
                status = checkRunResponse.body()?.status ?: ""
                if (status != "completed") {
                    println("Current status: $status. Waiting for completion...")
                    Thread.sleep(2000) // 2초 대기 후 다시 상태 확인

                    // 최대 대기 시간을 초과한 경우 에러 발생
                    if (System.currentTimeMillis() - startTime > maxWaitTimeMillis) {
                        throw RuntimeException("Timeout exceeded while waiting for completion.")
                    }
                    else if(status == "failed") {
                        throw RuntimeException("Run Failed")
                    }
                }
            } else {
                throw RuntimeException("Failed to check run status: ${checkRunResponse.errorBody()?.string()}")
            }
        } while (status != "completed")

        // 상태가 "Complete"가 된 후 getMessage 호출
        val messageResponse = apiService.getMessage(
            authHeader = "Bearer $gptAuth",
            threadId = threadId
        ).execute()

        if (messageResponse.isSuccessful) {
            return try{
                messageResponse.body()?.data?.get(0)?.content?.last()?.text?.value + ""
            }
            catch(e: Exception) {
                println(e)
                ""
            }
        } else {
            throw RuntimeException("Failed to get message: ${messageResponse.errorBody()?.string()}")
        }
    }
}

data class ResponseTreadDTO(
    val id : String,
    val thread_id : String
)

data class ThreadMessagesResponse(
    val data : List<MessageData>
)

data class MessageData(
    val content: List<ContentData>
)

data class ContentData(
    val type: String,
    val text: TextContent
)

data class TextContent(
    val value: String,
    val annotations: List<String> = emptyList()
)

//채팅 api용 DTO
data class MakeThreadDTO(
    val assistant_id: String,
    val thread: ThreadData? = null
)

data class ThreadData(
    val messages: List<MessageDTO>,
    val metadata: Map<String, String>? = null // 필요 시 metadata 추가
)

data class MessageDTO(
    val role: String,
    val content: String
)

//이미지 업로드용 DTO
data class FileDTO(
    val id: String,
    @JsonProperty("object") val obj : String,
    val bytes: Double,
    val created_at: Double,
    val filename: String,
    val purpose: String,
)

data class uploadDTO(
    val purpose: String,
    val filename: String,
    val bytes: Double,
    val mime_type: String
)

data class uploadResponse(
    val id: String,
    @JsonProperty("object") val obj : String,
    val bytes: String,
    val created_at: Double,
    val filename: String,
    val purpose: String,
    val status: String,
    val expires_at: Double
)

data class imageUploadDTO(
    val data : String
)

data class imageUploadResponse(
    val id: String,
    @JsonProperty("object") val obj: String,
    val created_at: Double,
    val upload_id: String
)

//이미지 api용 DTO
data class IMakeThreadDTO(
    val assistant_id: String,
    val thread: IThreadData? = null
)

data class IThreadData(
    val messages: List<IMessageDTO>,
    val metadata: Map<String, String>? = null // 필요 시 metadata 추가
)

data class IMessageDTO(
    val role: String,
    val content: List<ImageDTO>
)

data class ImageDTO(
    val type : String,
    val image_url : ImageURL
)

data class ImageURL(
    val url : String
)

data class CheckRunDTO(
    val status : String,
)

//응답용 DTO
data class TokenListDTO(
    @JsonProperty("Tokens") val Tokens : List<TokenDTO>
)

data class TokenDTO(
    @JsonProperty("Require") val Require : String,
    @JsonProperty("Subject") val Subject : String,
    @JsonProperty("Summary") val Summary : String,
    @JsonProperty("Token") val Token : String
)

//Feedback용 DTO
data class FeedbackDTO(
    val Tokens: List<TokenDTO>,
    val VenueInfos: List<VenueInfoDTO>,
    val Feedback: List<String>
)

data class Token(
    val Require: String,
    val Subject: String,
    val Summary: String,
    val Token: String
)

data class VenueInfoDTO(
    val venueId: Int,
    val name: String,
    val description: String,
    val style: List<String>,
    val caution: String
)

//Feedback 응답용 DTO
data class FeedbackResponseDTO(
    val Messages: List<FeedbackMessageDTO>,
    val Feedback: List<String>
)

data class FeedbackMessageDTO(
    val venueId: Int,
    val reason: List<String>,
    val caution: List<String>
)

//이미지 토큰용 DTO
data class ImageAnalysisResponse(
    val objects: List<AnalyzedObject>,
    val mood: String,
    val quality: Quality,
    val insights: String
)

data class AnalyzedObject(
    val name: String,
    val location: String,
    val size: String,
    val color: String,
    val shape: String,
    val texture: String,
    val action: String,
    val contextualRole: String,
    val symbolicSignificance: String
)

data class Quality(
    val clarity: String,
    val brightness: String,
    val contrast: String
)