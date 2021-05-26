package com.cscinfo.platform.constraint

import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultValidatorRegistry
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.validation.ValidatorRegistry
import org.grails.spring.beans.factory.InstanceFactoryBean
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class CascadeConstraintRegistrationSpec extends Specification implements GrailsUnitTest {

    DefaultConstraintRegistry anotherConstraintRegistry = Mock()
    DefaultConstraintEvaluator defaultConstraintEvaluator = Spy(new DefaultConstraintEvaluator(anotherConstraintRegistry, Stub(MappingContext), Collections.emptyMap()))
    DefaultValidatorRegistry defaultValidatorRegistry = Mock()
    DefaultConstraintRegistry defaultConstraintRegistry = Mock()

    def "register with multiple beans"() {
        setup:
        defineBeans {
            constraintEvaluator(InstanceFactoryBean, defaultConstraintEvaluator, ConstraintsEvaluator)
            validatorRegistry(InstanceFactoryBean, defaultValidatorRegistry, ValidatorRegistry)
            constraintRegistry(InstanceFactoryBean, defaultConstraintRegistry, ConstraintRegistry)
        }

        when:
        CascadeConstraintRegistration.register(applicationContext)

        then: 'cascading is added to DefaultConstraintEvaluator'
        1 * anotherConstraintRegistry.addConstraint(CascadeConstraint)

        and: 'cascading is added to DefaultValidatorRegistry'
        1 * defaultValidatorRegistry.addConstraint(CascadeConstraint)

        and: 'cascading is added to DefaultConstraintRegistry'
        1 * defaultConstraintRegistry.addConstraint(CascadeConstraint)
    }

    def "register with missing beans"() {
        setup:
        defineBeans {
        }

        when:
        CascadeConstraintRegistration.register(applicationContext)

        then: 'cascading is not added to DefaultConstraintEvaluator'
        0 * anotherConstraintRegistry.addConstraint(CascadeConstraint)

        and: 'cascading is not added to DefaultValidatorRegistry'
        0 * defaultValidatorRegistry.addConstraint(CascadeConstraint)

        and: 'cascading is not added to DefaultConstraintRegistry'
        0 * defaultConstraintRegistry.addConstraint(CascadeConstraint)
    }
}
