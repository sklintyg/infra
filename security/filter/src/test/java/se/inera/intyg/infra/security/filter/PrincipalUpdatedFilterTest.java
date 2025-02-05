/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.hash.HashCode;
import java.io.Serializable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * Tests so the session.setAttribute is invoked only when the underlying authentication principal has changed during
 * the filterChain.invoke.
 *
 * @author eriklupander
 */
@RunWith(MockitoJUnitRunner.class)
public class PrincipalUpdatedFilterTest {

    private static final String HSA_ID = "user-1";

    private PrincipalUpdatedFilter testee = new PrincipalUpdatedFilter();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpSession session;

    private Authentication auth1;
    private Authentication auth2;


    @Before
    public void setup() {
        when(request.getSession(false)).thenReturn(session);
        auth1 = buildAuthentication("state 1");
        auth2 = buildAuthentication("state 2");
    }

    @Test
    public void testIsUpdatedWhenPrincipalHasChanged() throws Exception {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(auth1);

        // Executes side-effect
        Answer<Void> ans = invocation -> {
            SecurityContextHolder.getContext().setAuthentication(auth2);
            return null;
        };

        doAnswer(ans).when(filterChain).doFilter(request, response);

        // Act
        testee.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(session, times(1)).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any());

    }

    @Test
    public void testIsNotUpdatedWhenPrincipalHasChanged() throws Exception {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(auth1);
        Answer<Void> ans = invocation -> null;
        doAnswer(ans).when(filterChain).doFilter(request, response);

        // Act
        testee.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(session, never()).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any());

    }

    @Test
    public void hashFunctionEqualsTest() {
        SomeUser u1 = new SomeUser("1", "x");
        SomeUser u2 = new SomeUser("1", "x");

        assertThat(testee.hashCode(u1), is(testee.hashCode(u2)));
    }

    @Test
    public void hashFunctionNotEqualsTest() {
        SomeUser someUser = new SomeUser("1", "x");

        HashCode hb = testee.hashCode(someUser);
        someUser.mutableThing = "y";
        HashCode ha = testee.hashCode(someUser);

        assertThat(hb, not(ha));
    }

    private Authentication buildAuthentication(String state) {
        return new AbstractAuthenticationToken(null) {
            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return new SomeUser(HSA_ID, state);
            }
        };
    }

    private static class SomeUser implements Serializable {

        String hsaId;
        String mutableThing;

        SomeUser(String hsaId, String mutableThing) {
            this.hsaId = hsaId;
            this.mutableThing = mutableThing;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SomeUser someUser)) {
                return false;
            }

            if (hsaId != null ? !hsaId.equals(someUser.hsaId) : someUser.hsaId != null) {
                return false;
            }
            return mutableThing != null ? mutableThing.equals(someUser.mutableThing) : someUser.mutableThing == null;
        }

        @Override
        public int hashCode() {
            int result = hsaId != null ? hsaId.hashCode() : 0;
            result = 31 * result + (mutableThing != null ? mutableThing.hashCode() : 0);
            return result;
        }
    }
}
