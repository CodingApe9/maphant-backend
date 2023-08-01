package com.tovelop.maphant.configure
import com.tovelop.maphant.dto.UserDTO
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
class MockupCustomUser(private val email: String, private var password: String?, private val userData: UserDTO) :
    UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
    }
    override fun getPassword(): String? = password
    override fun getUsername(): String = email
    fun getUserData(): UserDTO = userData
    fun getUserId(): Int = userData.id!!
    override fun isAccountNonExpired(): Boolean {
        return true
    }
    override fun isAccountNonLocked(): Boolean {
        return true
    }
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }
    override fun isEnabled(): Boolean {
        return true
    }
    fun zeroisePassword() {
        this.userData.password = ""
    }
}