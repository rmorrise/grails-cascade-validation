package com.cscinfo.platform.constraint

import grails.validation.ValidationErrors
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import spock.lang.Specification

/**
 * @author: rmorrise
 */
class CascadeValidationConstraintSpec extends Specification {
    CascadeValidationConstraint constraint = new CascadeValidationConstraint(owningClass: Parent, propertyName: 'property',
            constraintParameter: true)
    Parent parent
    ValidationErrors errors

    def setup() {
        parent = Mock(Parent)
        errors = Mock(ValidationErrors)
        parent.errors >> errors
    }

    def "GetName"() {
        expect:
        constraint.name == 'cascade'
    }

    def "test validateWithVetoing when constraint is set on non-validatable type"() {
        given:
        def value = "Some value"

        when:
        constraint.validateWithVetoing(parent, value, errors)

        then:
        thrown(NoSuchMethodException)
    }

    def "test validateWithVetoing when valid"() {
        given:
        def value = Mock(ValidateableProperty)

        when:
        def result = constraint.validateWithVetoing(parent, value, errors)

        then:
        1 * value.validate() >> true

        result == false
    }

    def "test validateWithVetoing when invalid"() {
        given:
        def value = Mock(ValidateableProperty)
        def childErrors = Mock(Errors)
        def rejected = Mock(Object)
        String[] codes = ['A', 'B']
        def defaultMessage = 'default'
        Object[] args = [Mock(Object)]
        def fieldError = new FieldError('obj', 'field', rejected, true, codes,
                args, defaultMessage)
        def fieldErrors = [fieldError]
        def parentName = 'foo'

        when:
        def result = constraint.validateWithVetoing(parent, value, errors)

        then:
        1 * value.validate() >> false
        1 * value.errors >> childErrors
        1 * childErrors.fieldErrors >> fieldErrors
        1 * errors.objectName >> parentName
        1 * errors.addError({
            it.objectName == parentName && it.bindingFailure == true && it.codes == codes && it.arguments == args && it
                    .defaultMessage == defaultMessage
        })

        result == true
    }

    def "test validateWithVetoing when invalid list element"() {
        given:
        def value = Mock(ValidateableProperty)
        def list = [value]
        def childErrors = Mock(Errors)
        def rejected = Mock(Object)
        String[] codes = ['A', 'B']
        def defaultMessage = 'default'
        Object[] args = [Mock(Object)]
        def fieldError = new FieldError('obj', 'field', rejected, true, codes,
                args, defaultMessage)
        def fieldErrors = [fieldError]
        def parentName = 'foo'

        when:
        def result = constraint.validateWithVetoing(parent, list, errors)

        then:
        1 * value.validate() >> false
        1 * value.errors >> childErrors
        1 * childErrors.fieldErrors >> fieldErrors
        1 * errors.objectName >> parentName
        1 * errors.addError({
            it.objectName == parentName && it.bindingFailure == true && it.codes == codes && it.arguments == args && it
                    .defaultMessage == defaultMessage
        })

        result == true
    }

    def "Supports: does not support non-validateable types"() {
        expect:
        !constraint.supports(String)
    }

    def "Supports: supports validateable types"() {
        expect:
        constraint.supports(ValidateableProperty)
    }

    def "Supports: supports collection types"() {
        expect:
        constraint.supports(List)
    }

    static class ValidateableProperty {
        Errors errors

        def validate() {
            true
        }
    }

    static class Parent {
        Errors errors
        ValidateableProperty property

        static constraints = {
            property(cascade: true)
        }
    }
}
