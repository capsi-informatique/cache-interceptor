package fr.rci.tools.cache.config;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.rci.tools.cache.CacheInterceptorConfiguration;
import fr.rci.tools.cache.EhcacheMethodCacheInterceptor;

@Configuration
public class CachePointConfig {
	
	@Autowired
	private EhcacheMethodCacheInterceptor interceptor ;
	
	@Autowired
	private CacheInterceptorConfiguration config;
	
	@Bean 
	public PointcutAdvisor additionnalPointCut() {
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor() ;
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut() ;
		pointcut.setExpression(config.getAdditionnalPointcut());
		Method m;
		try {
			m = EhcacheMethodCacheInterceptor.class.getMethod("cache", ProceedingJoinPoint.class);
			advisor.setAdvice(new AspectJAroundAdvice(m, pointcut, new SingletonAspectInstanceFactory(interceptor)));
			advisor.setPointcut(pointcut);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return advisor ;
	}
}