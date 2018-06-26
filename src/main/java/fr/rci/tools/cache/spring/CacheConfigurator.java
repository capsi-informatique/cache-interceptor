package fr.rci.tools.cache.spring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import fr.rci.tools.cache.CacheInterceptorConfiguration;

@ComponentScan("fr.rci.tools.cache")
@Configuration("SpringBootCacheConfiguration")
@EnableConfigurationProperties(CacheInterceptorConfiguration.class)
@EnableAspectJAutoProxy
public class CacheConfigurator {
   
} 