package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.PasswordEncoderBcrypt
import com.tovelop.maphant.dto.*
import com.tovelop.maphant.service.UserService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import com.tovelop.maphant.utils.ValidationHelper
import com.tovelop.maphant.utils.isSuccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class SignupController(@Autowired val userService: UserService) {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoderBcrypt

    //이메일 검증 api
    @PostMapping("/validation/email")
    fun validationEmail(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (!userService.isEmailValid(validationSignupDTO.email!!)) {
            return ResponseEntity.badRequest().body(Response.error("형식에 맞지 않는 이메일입니다."))
        }

        if (userService.isDuplicateEmail(validationSignupDTO.email)) {
            return ResponseEntity.badRequest().body(Response.error("이미 가입이 되어있는 이메일 입니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/validation/nickname")
    fun validationNickname(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidNickname(validationSignupDTO.nickname!!)) {
            return ResponseEntity.badRequest().body(Response.error("형식에 맞지 않는 별명입니다. ex) @domain.ac.kr 또는 @domain.edu"))
        }

        if (userService.isDuplicateNickname(validationSignupDTO.nickname)) {
            return ResponseEntity.badRequest().body(Response.error("이미 사용중인 별명입니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/validation/phoneNum")
    fun validationPhonenum(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidPhoneNum(validationSignupDTO.phonenum!!)) {
            return ResponseEntity.badRequest().body(Response.error("핸드폰 번호를 형식에 맞춰주세요. ex) 010-1234-5678"))
        }
        if (userService.isDuplicatePhoneNum(validationSignupDTO.phonenum)) {
            return ResponseEntity.badRequest().body(Response.error("이미 사용중인 핸드폰 번호입니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/validation/password")
    fun validationPassword(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidPassword(validationSignupDTO.password!!)) {
            return ResponseEntity.badRequest()
                .body(Response.error("비밀번호는 영문 소문자/대문자 1개 이상, 숫자와 특수문자를 포함하고, 최소 8자로 구성되어야 합니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/validation/passwordCheck")
    fun validationPasswordChk(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (validationSignupDTO.password != validationSignupDTO.passwordCheck) {
            return ResponseEntity.badRequest().body(Response.error("비밀번호와 비밀번호 확인이 동일하지 않습니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/signup")
    fun signup(@RequestBody signupDTO: SignupDTO): ResponseEntity<ResponseUnit> {
        val emailValidation = validationEmail(ValidationSignupDTO(email = signupDTO.email))
        if (!emailValidation.isSuccess()) {
            return emailValidation
        }

        val nicknameValidation = validationNickname(ValidationSignupDTO(nickname = signupDTO.nickname))
        if (!nicknameValidation.isSuccess()) {
            return nicknameValidation
        }

        val phoneNumValidation = validationPhonenum(ValidationSignupDTO(phonenum = signupDTO.phonenum))
        if (!phoneNumValidation.isSuccess()) {
            return phoneNumValidation
        }

        val passwordValidation = validationPassword(ValidationSignupDTO(password = signupDTO.password))
        if (!passwordValidation.isSuccess()) {
            return passwordValidation
        }

        val passwordChkValidation =
            validationPasswordChk(
                ValidationSignupDTO(
                    password = signupDTO.password,
                    passwordCheck = signupDTO.passwordCheck
                )
            )
        if (!passwordChkValidation.isSuccess()) {
            return passwordChkValidation
        }

        if (!ValidationHelper.isValidName(signupDTO.name)) {
            return ResponseEntity.badRequest().body(Response.error("이름을 형식에 맞춰주세요. ex) 홍길동"))
        }

        val universityId = userService.findUniversityIdBy(signupDTO.universityName)
        if (universityId == null) return ResponseEntity.badRequest().body(Response.error("해당 도메인을 가진 학교가 없습니다."))
        if (!userService.matchEmail(signupDTO.email, universityId)) {
            return ResponseEntity.badRequest().body(Response.error("이메일과 학교 이름을 확인해주세요."))
        }

        userService.signUp(signupDTO.toUserDTO(universityId, passwordEncoder))
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/login")
    fun login(@RequestBody login: LoginDTO): ResponseEntity<ResponseUnit> {
        //ID, PW DB 체크
        if (true /*입력받은 이메일, 비밀번호 DB에 있는 정보와 동일한 지 확인 */) {
            return ResponseEntity.badRequest().body(Response.error("이메일, 비밀번호를 확인해주세요."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }


    @PostMapping("/findemail")
    fun findEmail(@RequestBody findEmailDTO: FindEmailDTO): ResponseEntity<Any> {
        val emailcheck = userService.findEmailBy(findEmailDTO.sNo, findEmailDTO.phoneNo)
        if (emailcheck.isNullOrEmpty()) return ResponseEntity.badRequest()
            .body(Response.error<String>("일치하는 회원정보가 없습니다"))
        return ResponseEntity.ok(Response.success(mapOf<String, String>("email" to emailcheck)))
    }

    @PostMapping("/changepw")
    fun ChangePw(@RequestBody changePw: ChangePwDTO): ResponseEntity<ResponseUnit> {
        //이메일 DB 체크
        if (true /*이메일이 일치하지 않을 때*/) {
            return ResponseEntity.badRequest().body(Response.error("유효하지 않은 이메일입니다."))
        }

        // You might have to handle signService.sendEmail() based on its implementation and return type.

        return ResponseEntity.ok(Response.stateOnly(true))
        // 여기서 내 이메일을 session을 넘겨줄지, 아니면 data에 email을 넘겨줄지 결정 해야함.
    }

    @PostMapping("/changepw/authenticationcode")
    fun authenticationCode(@RequestBody newPw: NewPwDTO) {
        //인증번호 확인
        // You might have to handle this method based on its implementation and return type.
    }

    @PostMapping("/newpw")
    fun newPw(@RequestBody newPw: NewPwDTO): ResponseEntity<ResponseUnit> {
        //패스워드 입력, 검증
        // You might have to handle userService related function based on its implementation and return type.

        //DB 패스워드 치환
        return ResponseEntity.ok(Response.stateOnly(true))
    }
}
