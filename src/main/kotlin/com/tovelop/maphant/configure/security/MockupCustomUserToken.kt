package com.tovelop.maphant.configure
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
data class MockupCustomUserToken(
    private val email: String,
    private val password: String,
    private val userData: MockupCustomUser?,
    private val authorities: MutableCollection<out GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {
    override fun getCredentials(): String = password
    override fun getPrincipal(): Int = userData!!.getUserId()
    override fun isAuthenticated(): Boolean = userData != null
    override fun getDetails() = userData!!
    override fun getName() = userData!!.getUserData().nickname
}