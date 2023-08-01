package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.PasswordEncoderBcrypt
import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.*
import com.tovelop.maphant.service.UserService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import com.tovelop.maphant.utils.SendGrid
import com.tovelop.maphant.utils.ValidationHelper
import com.tovelop.maphant.utils.isSuccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class SignupController(@Autowired val userService: UserService, @Autowired val sendGrid: SendGrid) {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoderBcrypt

    @GetMapping("/")
    fun getUser(): ResponseEntity<Response<UserDataDTO>> {
        val auth = SecurityContextHolder.getContext().authentication
        if(auth != null && auth is TokenAuthToken && auth.isAuthenticated) {
            return ResponseEntity.ok(Response.success(auth.getUserData()))
        }

        return ResponseEntity.unprocessableEntity().body(Response.error("Invalid credentials"))
    }

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
            return ResponseEntity.badRequest().body(Response.error("별명은 3~20자의 영문, 한글, 숫자로 구성해야 합니다."))
        }

        if (userService.isDuplicateNickname(validationSignupDTO.nickname)) {
            return ResponseEntity.badRequest().body(Response.error("이미 사용중인 별명입니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/validation/phnum")
    fun validationPhNum(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidPhoneNum(validationSignupDTO.phNum!!)) {
            return ResponseEntity.badRequest().body(Response.error("핸드폰 번호를 형식에 맞춰주세요. ex) 010-1234-5678"))
        }
        if (userService.isDuplicatePhoneNum(validationSignupDTO.phNum)) {
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

    @PostMapping("/validation/passwordcheck")
    fun validationPasswordCheck(@RequestBody validationSignupDTO: ValidationSignupDTO): ResponseEntity<ResponseUnit> {
        if (validationSignupDTO.password != validationSignupDTO.passwordCheck) {
            return ResponseEntity.badRequest().body(Response.error("비밀번호와 비밀번호 확인이 동일하지 않습니다."))
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @GetMapping("/universitylist")
    fun listUniversity(): ResponseEntity<Response<List<String>>> {
        return ResponseEntity.ok().body(Response.success(userService.getAllUnivNames()))
    }

    @GetMapping("/categorylist")
    fun listCategory(): ResponseEntity<Response<List<String>>> {
        return ResponseEntity.ok().body(Response.success(userService.getAllCategories()))
    }

    @GetMapping("/majorlist")
    fun listMajor(): ResponseEntity<Response<List<String>>> {
        return ResponseEntity.ok().body(Response.success(userService.getAllMajors()))
    }

    @PostMapping("/selection/categorymajor")
    fun selectionCategory(@RequestBody categoryMajorDTO: CategoryMajorDTO): ResponseEntity<ResponseUnit> {
        userService.insertCategoryMajorByEmail(
            categoryMajorDTO.email, categoryMajorDTO.category, categoryMajorDTO.major
        )

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

        val passwordValidation = validationPassword(ValidationSignupDTO(password = signupDTO.password))
        if (!passwordValidation.isSuccess()){
            return ResponseEntity.badRequest().body(Response.error("비밀번호는 영문 소문자/대문자 1개 이상, 숫자와 특수문자를 포함하고, 최소 8자로 구성되어야 합니다."))
        }

        val passwordChkValidation = validationPasswordCheck(
            ValidationSignupDTO(
                password = signupDTO.password, passwordCheck = signupDTO.passwordCheck
            )
        )
        if (!passwordChkValidation.isSuccess()) {
            return passwordChkValidation
        }

        if (!ValidationHelper.isValidName(signupDTO.name)) {
            return ResponseEntity.badRequest().body(Response.error("이름을 형식에 맞춰주세요. ex) 홍길동"))
        }

        val univId = userService.findUniversityIdBy(signupDTO.univName)
            ?: return ResponseEntity.badRequest().body(Response.error("해당 도메인을 가진 학교가 없습니다."))
        if (!userService.matchEmail(signupDTO.email, univId)) {
            return ResponseEntity.badRequest().body(Response.error("이메일과 학교 이름을 확인해주세요."))
        }

        sendGrid.sendSignUp(signupDTO.email)
        userService.signUp(signupDTO.toUserDTO(univId, passwordEncoder))
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    //개인정보 수정 페이지 접근 전, 본인 확인 절차: 비밀번호 확인
    @PostMapping("/changeinfo/identification")
    fun identification(@RequestBody identificationDTO: IdentificationDTO): ResponseEntity<ResponseUnit> {
        val oldPassword = userService.findPasswordByEmail(identificationDTO.email)
        if (!passwordEncoder.matches(identificationDTO.password, oldPassword)) {
            return ResponseEntity.badRequest().body(Response.error("비밀번호를 확인해주세요."))
        }
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/changeinfo/olddata")
    fun changeInfo(@RequestBody changeInfoDTO: ChangeInfoDTO): ResponseEntity<Response<UserDTO>> {
        val userData = userService.getUser(listOf(changeInfoDTO.email))!!

        return ResponseEntity.ok().body(Response.success(userData))
    }

    @PostMapping("/changeinfo/nickname")
    fun changeInfoNickname(@RequestBody changeInfoDTO: ChangeInfoDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidNickname(changeInfoDTO.nickname!!)) {
            return ResponseEntity.badRequest().body(Response.error("별명은 3~20자의 영문, 한글, 숫자로 구성해야 합니다."))
        }

        if (userService.isDuplicateNickname(changeInfoDTO.nickname)) {
            return ResponseEntity.badRequest().body(Response.error("이미 사용중인 별명입니다."))
        }

        //email로 nickname db저장
        userService.updateUserNicknameByEmail(changeInfoDTO.email, changeInfoDTO.nickname)

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/changeinfo/phnum")
    fun changePhonenum(@RequestBody changeInfoDTO: ChangeInfoDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidPhoneNum(changeInfoDTO.phNum!!)) {
            return ResponseEntity.badRequest().body(Response.error("핸드폰 번호를 형식에 맞춰주세요. ex) 010-1234-5678"))
        }

        userService.updateUserPhoneNumByEmail(changeInfoDTO.email, changeInfoDTO.phNum)

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/changeinfo/password")
    fun changeInfoPwd(@RequestBody changeInfoDTO: ChangeInfoDTO): ResponseEntity<ResponseUnit> {
        val oldPassword = userService.findPasswordByEmail(changeInfoDTO.email)

        if (!ValidationHelper.isValidPassword(changeInfoDTO.newPassword!!)) {
            return ResponseEntity.badRequest()
                .body(Response.error("비밀번호는 영문 소문자/대문자 1개 이상, 숫자와 특수문자를 포함하고, 최소 8자로 구성되어야 합니다."))
        }

        if (changeInfoDTO.newPassword != changeInfoDTO.newPasswordCheck) {
            return ResponseEntity.badRequest().body(Response.error("비밀번호와 비밀번호 확인이 동일하지 않습니다."))
        }

        if (passwordEncoder.matches(changeInfoDTO.newPasswordCheck, oldPassword)) {
            return ResponseEntity.badRequest().body(Response.error("기존 비밀번호입니다."))
        }

        userService.updateUserPasswordByEmail(
            changeInfoDTO.email, passwordEncoder.encode(changeInfoDTO.newPasswordCheck)
        )

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/changepw/sendemail")
    fun changePw(@RequestBody changePw: ChangePasswordDTO): ResponseEntity<ResponseUnit> {
        if (!userService.isEmailValid(changePw.email)) {
            return ResponseEntity.badRequest().body(Response.error("형식에 맞지 않는 이메일입니다."))
        }

        if (!userService.isDuplicateEmail(changePw.email)) {
            return ResponseEntity.badRequest().body(Response.error("유저 정보가 없습니다."))
        } else{
            sendGrid.sendChangePW(changePw.email)
        }

        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/changepw/authenticationcode")
    fun authenticationCode(@RequestBody emailAuthDTO: EmailAuthDTO): ResponseEntity<ResponseUnit> {
        val isSendEmail = sendGrid.confirmEmailToken(emailAuthDTO.email, emailAuthDTO.authCode ?: "")

        return if (isSendEmail) ResponseEntity.ok(Response.stateOnly(true)) else ResponseEntity.badRequest()
            .body(Response.error("인증번호가 일치하지 않습니다."))
    }

    @PostMapping("/changepw/newpassword")
    fun newPw(@RequestBody newPasswordDTO: NewPasswordDTO): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isValidPassword(newPasswordDTO.password)) {
            return ResponseEntity.badRequest()
                .body(Response.error("비밀번호는 영문 소문자/대문자 1개 이상, 숫자와 특수문자를 포함하고, 최소 8자로 구성되어야 합니다."))
        }

        if (newPasswordDTO.password != newPasswordDTO.passwordChk) {
            return ResponseEntity.badRequest().body(Response.error("비밀번호와 비밀번호 확인이 동일하지 않습니다."))
        }

        val ogPwd = userService.findPasswordByEmail(newPasswordDTO.email)
        if (passwordEncoder.matches(newPasswordDTO.passwordChk, ogPwd)) {
            return ResponseEntity.badRequest().body(Response.error("기존 비밀번호입니다."))
        }

        userService.updateUserPasswordByEmail(newPasswordDTO.email, passwordEncoder.encode(newPasswordDTO.passwordChk))

        return ResponseEntity.ok(Response.stateOnly(true))
    }
}
