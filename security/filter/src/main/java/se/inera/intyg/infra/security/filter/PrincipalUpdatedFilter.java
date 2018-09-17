/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter checks if the user Principal has changed (new vald vardenhet, some consent given etc). If true,
 * the wrapped RedisSession is "touched" using session.setAttribute("SPRING_SECURITY_CONTEXT", context) which
 * then will trigger a diff in the springSessionRepositoryFilter forcing an update of the Principal in the redis store.
 *
 * ONLY use this filter if you're using Spring Session with Redis!
 *
 * This filter should run directly AFTER the spring security filters so it has access to the Spring Security
 * SecurityContextHolder.getContext().
 *
 * @author eriklupander
 */
public class PrincipalUpdatedFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        int beforeHash = -1;
        boolean hasAuthentication = hasAuthentication();

        // If we're authenticated, calculate hashcode of current user principal.
        if (hasAuthentication) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            beforeHash = auth.getPrincipal().hashCode();
        }

        // Invoke next filter in chain.
        filterChain.doFilter(request, response);

        // If we were authenticated and calculated a hash before invoke...
        if (hasAuthentication && beforeHash != -1) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            int afterHash = auth.getPrincipal().hashCode();
            // Check if principal hash has changed
            if (beforeHash != afterHash) {
                updatePrincipalOnSession(request);
            }
        }
    }

    private void updatePrincipalOnSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
        }
    }

    private boolean hasAuthentication() {
        return SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null;
    }
}
