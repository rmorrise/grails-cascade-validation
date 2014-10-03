package com.cscinfo.platform.constraint

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

    String getName() { NAME }

    @Override
    protected boolean processValidateWithVetoing(target, propertyValue, Errors errors) {
        if (!(propertyValue instanceof Collection)) {
            return validateValue(target, propertyValue, errors)
        }

        boolean result = false
        for (value in propertyValue) {
            result = validateValue(target, value, errors) || result
        }
        result
    }

    private boolean validateValue(target, propertyValue, errors) {
        if (!propertyValue.respondsTo('validate')) {
            throw new NoSuchMethodException("Error validating field [${constraintPropertyName}]. Unable to apply 'cascade' constraint on [${propertyValue.class}] because the object does not have a validate() method. If the object is a command object, you may need to add the @Validateable annotation to the class definition.")
        }

        if (propertyValue.validate()) {
            return false
        }

        String objectName = target.errors.objectName
        propertyValue.errors.fieldErrors.each { FieldError fieldError ->
            String field = "${propertyName}.${fieldError.field}"
            errors.addError(new FieldError(objectName, field, fieldError.rejectedValue, fieldError.bindingFailure,
                fieldError.codes, fieldError.arguments, fieldError.defaultMessage))
        }
        return true
    }

    boolean supports(Class type) {
        Collection.isAssignableFrom(type) || type.metaClass.respondsTo(type, 'validate')
    }
}
