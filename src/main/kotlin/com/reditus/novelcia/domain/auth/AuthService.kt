package com.reditus.novelcia.domain.auth

import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.domain.user.UserCommand
import com.reditus.novelcia.domain.user.UserModel
import com.reditus.novelcia.domain.user.port.UserReader
import com.reditus.novelcia.domain.user.port.UserWriter
import com.reditus.novelcia.global.exception.ElementConflictException
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
) {

    fun emailSignup(command: UserCommand.Create) = transactional {
        if (userReader.existsByEmail(command.email)) {
            throw ElementConflictException("이미 가입된 이메일입니다.")
        }
        val encodedCommand = command.copyWith(
            encodedPassword = bCryptPasswordEncoder.encode(command.password)
        )
        val user = User.create(encodedCommand)
        userWriter.save(user)
    }

    fun emailSignIn(email: String, password: String): UserModel = readOnly {
        val user = userReader.findByEmail(email) ?: throw ElementConflictException("가입되지 않은 이메일입니다.")
        if (!bCryptPasswordEncoder.matches(password, user.encodedPassword)) {
            throw ElementConflictException("비밀번호가 일치하지 않습니다.")
        }
        UserModel.from(user)(this)
    }
}