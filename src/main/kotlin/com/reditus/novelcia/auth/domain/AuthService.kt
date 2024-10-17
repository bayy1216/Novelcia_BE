package com.reditus.novelcia.auth.domain

import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.user.domain.UserCommand
import com.reditus.novelcia.user.application.UserModel
import com.reditus.novelcia.global.exception.ElementConflictException
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
) {

    fun emailSignup(command: UserCommand.Create) = transactional {
        if (userRepository.existsByEmail(command.email)) {
            throw ElementConflictException("이미 가입된 이메일입니다.")
        }
        val encodedCommand = command.copyWith(
            encodedPassword = bCryptPasswordEncoder.encode(command.password)
        )
        val user = User.create(encodedCommand)
        userRepository.save(user)
    }

    fun emailSignIn(email: String, password: String): UserModel = readOnly {
        val user = userRepository.findByEmail(email) ?: throw ElementConflictException("가입되지 않은 이메일입니다.")
        if (!bCryptPasswordEncoder.matches(password, user.encodedPassword)) {
            throw ElementConflictException("비밀번호가 일치하지 않습니다.")
        }
        UserModel.from(user)(this)
    }
}