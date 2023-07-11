package com.tovelop.maphant.configure.security

import com.tovelop.maphant.configure.MockupCustomUser
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority


class SecurityLoginAuthToken(private val email: String,
                             private val password: String,
                             private val userData: MockupCustomUser? = null,
                             authorities: MutableCollection<out GrantedAuthority>? = null
): AbstractAuthenticationToken(authorities)
{
    override fun getCredentials() = password

    override fun getPrincipal() = email

    override fun isAuthenticated(): Boolean {
        return userData != null
    }
}
