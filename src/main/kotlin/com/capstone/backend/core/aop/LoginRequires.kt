package com.capstone.backend.core.aop

import jakarta.servlet.http.HttpSession
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresLogin

@Aspect
@Component
class LoginAspect(private val session: HttpSession) {

    @Before("@annotation(com.capstone.backend.core.aop.RequiresLogin)")
    fun checkLogin() {
        val loggedInUserId = session.getAttribute("userId") ?: throw IllegalStateException("로그인이 필요합니다.")
        // 로그인된 사용자 ID가 세션에 없을 경우 예외를 발생시킴
    }
}