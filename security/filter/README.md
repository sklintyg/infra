# Security filters

This tiny module contains custom Spring Security filter that adds various security headers to HTTP responses.

# Setting up Spring Session backed by Redis
Quick documentation on how to set up Spring Session with redis-backed session storage.

##### Add spring-session as dependency

To build.gradle

    springSessionVersion = "1.3.3.RELEASE"

To web/build.gradle

    compile "org.springframework.session:spring-session:${springSessionVersion}"
    
##### Add the Redis HTTP session configurer

To securityContext.xml or other spring xml config file. 

      <!-- Setup for Spring Session backed by redis -->
      <bean class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
          <property name="redisNamespace" value="${app.name}"/>
          <property name="redisFlushMode" value="IMMEDIATE" />
      </bean>

##### Add the session repository filter as the first filter to web.xml or @Configuration class

      <filter>
        <filter-name>springSessionRepositoryFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
      </filter>
      <filter-mapping>
        <filter-name>springSessionRepositoryFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>ERROR</dispatcher>
      </filter-mapping>


OR

    // Spring session filter
    FilterRegistration.Dynamic springSessionRepositoryFilter = servletContext.addFilter("springSessionRepositoryFilter",
            DelegatingFilterProxy.class);
    springSessionRepositoryFilter.addMappingForUrlPatterns(null, false, "/*");

##### Adding support for propagating principal changes to Redis

If your application stores stuff in the user principal, we need an extra custom filter to make sure Redis stores those changes.

Add the following filter _directly after_ the springSecurityFilterChain:

    FilterRegistration.Dynamic principalUpdatedFilter = servletContext.addFilter("principalUpdatedFilter",
            DelegatingFilterProxy.class);
    principalUpdatedFilter.setInitParameter("targetFilterLifecycle", "true");
    principalUpdatedFilter.addMappingForUrlPatterns(null, false, "/*");
    
The filter needs to be declared in @Configuration class or <bean> xml:

    @Bean
    public PrincipalUpdatedFilter principalUpdatedFilter() {
        return new PrincipalUpdatedFilter();
    }
