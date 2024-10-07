package com.capstone.backend.Service

import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface Word2VecApiService {

    // FastAPI의 "/word2vec" 엔드포인트로 POST 요청을 보냅니다
    @POST("/word2vec")
    suspend fun getWordVectors(
        @Body request: Word2VecRequest
    ): Map<String, List<Double>> // 단어와 벡터를 담은 맵을 반환
}

// 요청 데이터 클래스
data class Word2VecRequest(
    val words: List<String>
)

// 응답 데이터 클래스 (단어와 벡터를 담는 구조)
typealias WordVectorMap = Map<String, List<Double>>




@Service
class Word2VectorService {

    // Retrofit 인스턴스 생성
    private final val word2vecHost = System.getProperty("Word2VecHost")
    private final val retrofit = Retrofit.Builder()
        .baseUrl(word2vecHost) // FastAPI 서버 URL
        .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 Gson 컨버터
        .build()

    // API 서비스 생성
    val apiService = retrofit.create(Word2VecApiService::class.java)


    suspend fun getWord2Vector(words: List<String>): WordVectorMap {
        return try {
            // 3. API 호출
            apiService.getWordVectors(Word2VecRequest(words))
        } catch (e: Exception) {
            // 오류 처리
            e.printStackTrace()
            emptyMap()
        }
    }
}