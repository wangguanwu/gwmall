package com.gw.gwmall.ordercurrent.aop;/*
package com.gw.gwmall.ordercurrent.aop;

import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;



@Aspect
@Component
@Order(-1)
public class ShardingTxTypeAspj {

    @Pointcut(value = "execution(* com.tuling.tulingmall.service.impl.Transaction2PcServiceImpl.insertOrder(..))")
    public void pointCut(){

    }

    @Before(value = "pointCut()")
    public void methodBefore(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        System.out.println("执行目标方法【"+methodName+"】的<前置通知>,入参"+ Arrays.asList(joinPoint.getArgs()));
        Annotation[] declaredAnnotations = methodSignature.getMethod().getDeclaredAnnotations();
        ShardingTransactionType declaredAnnotation = methodSignature.getMethod().getDeclaredAnnotation(ShardingTransactionType.class);
        System.out.println(declaredAnnotation.value());
        TransactionTypeHolder.set(declaredAnnotation.value());
    }
}
*/
