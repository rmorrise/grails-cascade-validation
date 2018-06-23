package com.cscinfo.platform.constraint

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.validation.constraints.AbstractVetoingConstraint
import org.springframework.context.MessageSource
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
@CompileStatic
class CascadeValidationConstraint extends AbstractVetoingConstraint {
    static final String NAME = "cascade"

    CascadeValidationConstraint(Class<?> constraintOwningClass, String constraintPropertyName, Object constraintParameter, MessageSource messageSource) {
        super(constraintOwningClass, constraintPropertyName, constraintParameter, messageSource)
    }

    /**
     * Not really sure what this is for, as the process validate still gets called...
     * probably will ask about it at GR8Conf
     *
     * @param constraintParameter
     * @return
     */
    @Override
    protected Object validateParameter(Object constraintParameter) {
        return constraintParameter
    }

    String getName() { NAME }

    @Override
    protected boolean processValidateWithVetoing(target, propertyValue, Errors errors) {
        boolean result = false

        if (propertyValue instanceof Collection) {
            propertyValue.eachWithIndex { item, pvIdx ->
                result = validateValue(target, item, errors, pvIdx) || result
            }
        } else {
            result = validateValue(target, propertyValue, errors)
        }

        return result
    }

    @CompileDynamic
    private boolean validateValue(target, value, errors, index = null) {
        if (!value.respondsTo('validate')) {
            throw new NoSuchMethodException("Error validating field [${constraintPropertyName}]. Unable to apply 'cascade' constraint on [${value.class}] because the object does not have a validate() method. If the object is a command object, you may need to add the @Validateable annotation to the class definition.")
        }

        if (value.validate()) {
            return false
        }

        String objectName = target.errors.objectName
        Errors childErrors = value.errors
        List<FieldError> childFieldErrors = childErrors.fieldErrors
        childFieldErrors.each { FieldError childFieldError ->
            String field
            if(index != null) {
                field = "${propertyName}.${index}.${childFieldError.field}"
            } else {
                field = "${propertyName}.${childFieldError.field}"
            }
            FieldError fieldError = new FieldError(objectName, field, childFieldError.rejectedValue, childFieldError.bindingFailure, childFieldError.codes, childFieldError.arguments, childFieldError.defaultMessage)
            errors.addError(fieldError)
        }
        return true
    }

    boolean supports(Class type) {
        Collection.isAssignableFrom(type) || type.metaClass.respondsTo(type, 'validate')
    }
}