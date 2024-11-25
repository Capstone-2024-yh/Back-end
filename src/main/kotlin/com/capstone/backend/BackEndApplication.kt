package com.capstone.backend

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackEndApplication

fun main(args: Array<String>) {
//   환경변수 읽어와야함 .env로 관리함
    val dotenv = Dotenv.configure().ignoreIfMissing().load()
    System.setProperty("DB_HOST", dotenv["DB_HOST"])
    System.setProperty("DB_USER", dotenv["DB_USER"])
    System.setProperty("DB_PASS", dotenv["DB_PASS"])
    System.setProperty("Word2VecHost", dotenv["Word2VecHost"])
    System.setProperty("GPT_AUTH", dotenv["GPT_AUTH"])
    System.setProperty("GPT_ASSISTANCE", dotenv["GPT_ASSISTANCE"])
    System.setProperty("GPT_TOKEN", dotenv["GPT_TOKEN"])
    System.setProperty("GPT_VENUE_TOKEN", dotenv["GPT_VENUE_TOKEN"])
    System.setProperty("GPT_IMAGE_TOKEN", dotenv["GPT_IMAGE_TOKEN"])

    runApplication<BackEndApplication>(*args)
}
