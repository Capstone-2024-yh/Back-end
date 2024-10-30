package com.capstone.backend.Service

import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface GptApiService {
    @POST("/v1/threads/runs")
    @Headers("Content-Type: application/json", "OpenAI-Beta: assistants=v2")
    fun getResponse(
        @Header("Authorization") authHeader: String,
        @Body request: MakeThreadDTO
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
}

@Service
class GptService {
    private final val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/") // GptAPI 서버 URL
        .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 Gson 컨버터
        .build()

    val apiService = retrofit.create(GptApiService::class.java)

    fun getResponse(str: String): String {
        return try {
            val gptAuth = System.getProperty("GPT_AUTH")
            val assistanceId = System.getProperty("GPT_ASSISTANCE")

            // getResponse API 호출
            val threadRunResponse = apiService.getResponse(
                authHeader = "Bearer $gptAuth",
                MakeThreadDTO(
                    assistant_id = assistanceId,
                    thread = ThreadData(
                        messages = listOf(
                            MessageDTO(
                                role = "user",
                                content = str
                            )
                        )
                    )
                )
            ).execute() // 동기 호출

            if (threadRunResponse.isSuccessful) {
                val threadRun = threadRunResponse.body()
                val threadId = threadRun!!.thread_id
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
                    return messageResponse.body()?.data?.get(0)?.content?.last()?.text?.value ?: ""
                } else {
                    throw RuntimeException("Failed to get message: ${messageResponse.errorBody()?.string()}")
                }
            } else {
                throw RuntimeException("Failed to get response: ${threadRunResponse.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
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

data class CheckRunDTO(
    val status : String,
)