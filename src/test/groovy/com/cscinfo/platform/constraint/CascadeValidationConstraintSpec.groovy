package com.cscinfo.platform.constraint

import com.cscinfo.platform.constraint.support.ValidateableParent
import com.cscinfo.platform.constraint.support.ValidateableProperty
import grails.validation.ValidationErrors
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import spock.lang.Specification
/**
 * @author: rmorrise
 * @author Eric Kelm
 */
class CascadeValidationConstraintSpec extends Specification {
    CascadeConstraint  constraint
    ValidateableParent parent
    ValidationErrors   errors

    def setup() {
        parent = Mock(ValidateableParent)
        errors = Mock(ValidationErrors)
        parent.errors >> errors
    }

    def "constraint name should be cascade"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        expect:
        constraint.name == 'cascade'
    }

    def "validateWithVetoing fails when constraint is set on non-validatable type"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
            )

        def target = "Some value"

        when:
        constraint.validate(parent, target, errors)

        then:
        thrown(NoSuchMethodException)
    }

    def "validateWithVetoing returns valid when constraint is set to validateable type and constraints pass"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        def target = Mock(ValidateableProperty)

        when:
        def result = constraint.validate(parent, target, errors)

        then:
        1 * target.validate() >> true
            0 * errors.addError(_)
    }

    def "validateWithVetoing returns invalid when constraint is set to validateable type and constraints fail"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        def target = Mock(ValidateableProperty)
        def childErrors = Mock(Errors)
        def rejected = Mock(Object)
        String[] codes = ['A', 'B']
        def defaultMessage = 'default'
        Object[] args = [Mock(Object)]

        def field = 'field'
        def fieldError = new FieldError('obj', field, rejected, true, codes,
                args, defaultMessage)
        def fieldErrors = [fieldError]
        def parentName = 'foo'

        when:
        def result = constraint.validate(parent, target, errors)

        then:
        1 * target.validate() >> false
        1 * target.errors >> childErrors
        1 * childErrors.fieldErrors >> fieldErrors
        1 * errors.objectName >> parentName
        1 * errors.addError({
            it.objectName == parentName &&
                    it.field == "property." + field &&
                    it.bindingFailure == true &&
                    it.codes == codes &&
                    it.arguments == args &&
                    it.defaultMessage == defaultMessage
        })
    }

    def "validateWithVetoing returns invalid when constraint is set to validateable type and constraints fail on list"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        def child1 = Mock(ValidateableProperty)
        def child2 = Mock(ValidateableProperty)
        def child1Errors = Mock(Errors)
        def child2Errors = Mock(Errors)
        def target = [child1, child2]
        def rejected = Mock(Object)
        String[] codes = ['A', 'B']
        def defaultMessage = 'default'
        Object[] args = [Mock(Object)]
        def field = 'field'
        def fieldError = new FieldError('obj', field, rejected, true, codes,
                args, defaultMessage)
        def fieldErrors = [fieldError]
        def parentName = 'foo'

        when:
        def result = constraint.validate(parent, target, errors)

        then:
        1 * child1.validate() >> false
        1 * child1.errors >> child1Errors
        1 * child2.validate() >> false
        1 * child2.errors >> child2Errors
        1 * child1Errors.fieldErrors >> fieldErrors
        1 * child2Errors.fieldErrors >> fieldErrors
        target.size() * errors.objectName >> parentName
        2 * errors.addError(_)
    }

    def "constraint does not support non-validateable types"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        expect:
        !constraint.supports(String)
    }

    def "constraint supports validateable types"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        expect:
        constraint.supports(ValidateableProperty)
    }

    def "constraint supports collection types"() {
        given:
        constraint = new CascadeConstraint(
                ValidateableParent,
                'property',
                true,
                null
        )

        expect:
        constraint.supports(List)
    }
}