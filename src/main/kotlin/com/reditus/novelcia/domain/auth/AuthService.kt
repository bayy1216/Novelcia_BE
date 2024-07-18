package com.reditus.novelcia.domain.auth

import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.domain.user.UserCommand
import com.reditus.novelcia.domain.user.UserReader
import com.reditus.novelcia.domain.user.UserWriter
import com.reditus.novelcia.global.exception.ElementConflictException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
) {

    @Transactional
    fun emailSignup(command: UserCommand.Create) {
        if(userReader.existsByEmail(command.email)) {
            throw ElementConflictException("이미 가입된 이메일입니다.")
        }
        val encodedCommand = command.copyWith(
            encodedPassword = bCryptPasswordEncoder.encode(command.password)
        )
        val user = User.create(encodedCommand)
        userWriter.save(user)
    }
}