package com.cscinfo.platform.constraint

import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultValidatorRegistry
import org.grails.datastore.mapping.validation.ValidatorRegistry
import org.springframework.context.ApplicationContext

class CascadeConstraintRegistration {
    static void register(ApplicationContext applicationContext) {
        registerCascadeConstraintOnBeans(applicationContext, ConstraintsEvaluator, DefaultConstraintEvaluator) {
            it.constraintRegistry
        }
        registerCascadeConstraintOnBeans(applicationContext, ValidatorRegistry, DefaultValidatorRegistry)
        registerCascadeConstraintOnBeans(applicationContext, ConstraintRegistry, ConstraintRegistry)
    }

    private static void registerCascadeConstraintOnBeans(ApplicationContext applicationContext,
                                                         Class interfaceClass,
                                                         Class clazz,
                                                         Closure<ConstraintRegistry> closure = Closure.IDENTITY) {
        Map<String, ?> evaluators = applicationContext.getBeansOfType(interfaceClass)
        evaluators.each { name, evaluator ->
            if (clazz.isAssignableFrom(evaluator.getClass())) {
                ConstraintRegistry reg = closure.call(evaluator)
                reg?.addConstraint(CascadeConstraint)
            }
        }
    }
}
