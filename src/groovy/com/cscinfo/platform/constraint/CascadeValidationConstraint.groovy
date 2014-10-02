package com.cscinfo.platform.constraint

import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.validation.AbstractVetoingConstraint
import org.springframework.validation.Errors
import org.springframework.validation.FieldError

/**
 * Establishes a 'cascade' constraint property for validateable objects. If "cascade:true"
 * is set on a nested object, the nested object's validate() method will be invoked and the
 * results will be reported as part of the parent object's validation.
 *
 * Based on a blog article by Eric Kelm, with modifications by Russell Morrisey.
 *
 * @see: http://asoftwareguy.com/2013/07/01/grails-cascade-validation-for-pogos/
 * @author Eric Kelm
 * @author Russell Morrisey
 */
class CascadeValidationConstraint extends AbstractVetoingConstraint {
    public static final String NAME = "cascade"

    /**
     * @return The name of the constraint
     */
    @Override
    String getName() {
        return NAME
    }

    @Override
    protected boolean processValidateWithVetoing(final Object target, final Object propertyValue, final Errors errors) {
        def result
        if (propertyValue instanceof Collection) {
            result = false
            propertyValue.each {
                result = validateValue(target, it, errors) || result
            }
        }
        else {
            result = validateValue(target, propertyValue, errors)
        }
        return result
    }

    private boolean validateValue(target, propertyValue, errors) {
        if (!propertyValue.respondsTo('validate')) {
            throw new NoSuchMethodException("Error validating field [${constraintPropertyName}]. Unable to apply 'cascade' constraint on [${propertyValue.class}] because the object does not have a validate() method. If the object is a command object, you may need to add the @Validateable annotation to the class definition.")
        }

        if (!propertyValue.validate()) {
            propertyValue.errors.fieldErrors.each {
                String field = "${propertyName}.${it.field}"
                def fieldError = new FieldError(target.errors.objectName, field, it.rejectedValue, it.bindingFailure, it.codes,
                        it.arguments, it.defaultMessage)
                ((ValidationErrors) errors).addError(fieldError)
            }
            return true
        }
        return false
    }

    /**
     * Returns whether the constraint supports being applied against the specified type;
     *
     * @param type The type to support
     * @return true if the constraint can be applied against the specified type
     */
    @Override
    boolean supports(final Class type) {
        Collection.isAssignableFrom(type) || type.metaClass.respondsTo(type, 'validate')
    }
}
