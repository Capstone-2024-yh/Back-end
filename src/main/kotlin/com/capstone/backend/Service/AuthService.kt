package com.capstone.backend.Service

import com.capstone.backend.Entity.User
import com.capstone.backend.Repository.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.mindrot.jbcrypt.BCrypt


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val session: HttpSession
) {

    // 회원가입
    @Transactional
    fun registerUser(username: String, email: String, password: String): User {
        //해쉬된 상태로 저장 비밀번호에 해쉬키 포함 되어 있음
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val user = User(
            username = username,
            email = email,
            passwordHash = hashedPassword
        )
        return userRepository.save(user)
    }

    // 로그인
    @Transactional(readOnly = true)
    fun login(email: String, password: String): Pair<Boolean, Int> {
        val user = userRepository.findByEmail(email)
        val hashedPassword = user?.passwordHash ?: return Pair(false, 0)

        // 비밀번호 일치 여부 확인
        val isOk = BCrypt.checkpw(password, hashedPassword)
        if (isOk) {
            // 세션에 사용자 ID 저장하여 로그인 상태 유지
            session.setAttribute("userId", user.userId)
        }
        return Pair(isOk, user.userId)
    }

    // 로그아웃
    fun logout() {
        // 세션 무효화
        session.invalidate()
    }

    // 현재 로그인 상태 확인
    fun isLoggedIn(): Boolean {
        // 세션에 사용자 ID가 존재하면 로그인 상태로 간주
        return session.getAttribute("userId") != null
    }
}
