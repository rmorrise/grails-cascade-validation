package com.cscinfo.platform.constraint

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.validation.constraints.AbstractConstraint
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
class CascadeConstraint extends AbstractConstraint {
    boolean enabled = true

    static final String CASCADE_CONSTRAINT = "cascade"

    CascadeConstraint(Class<?> constraintOwningClass, String constraintPropertyName, Object constraintParameter, MessageSource messageSource) {
        super(constraintOwningClass, constraintPropertyName, constraintParameter, messageSource)

        if (!(constraintParameter instanceof Boolean)) {
            throw new IllegalArgumentException("Parameter for constraint [$CASCADE_CONSTRAINT] of property [$constraintPropertyName] of class [$constraintOwningClass] must be a boolean")
        }

        this.enabled = (boolean) constraintParameter
    }

    @Override
    protected Object validateParameter(Object constraintParameter) {
        return constraintParameter
    }

    boolean supports(Class type) {
        Collection.isAssignableFrom(type) || type.metaClass.respondsTo(type, 'validate')
    }

    String getName() {
        return CASCADE_CONSTRAINT
    }

    protected void processValidate(Object target, Object propertyValue, Errors errors) {

        boolean result = false

        if (propertyValue instanceof Collection) {
            propertyValue.eachWithIndex { item, pvIdx ->
                validateValue(target, item, errors, pvIdx) || result
            }
        } else {
            validateValue(target, propertyValue, errors)
        }
    }

    /**
     * Processes the validation of the propertyValue, against the checks patterns set, and setting and calling rejectValue
     * if the propertyValue matches any of the patterns in the checks list.
     *
     * @param target The target field to verify.
     * @param propertyValue the property value of the field.
     * @param errors Errors to be sent by rejectValues,.
     */
    @CompileDynamic
    private void validateValue(target, value, errors, index = null) {
        if (!value.respondsTo('validate')) {
            throw new NoSuchMethodException("Error validating field [${constraintPropertyName}]. Unable to apply 'cascade' constraint on [${value.class}] because the object does not have a validate() method. If the object is a command object, you may need to add the @Validateable annotation to the class definition.")
        }

        if (value.validate()) {
            return
        }

        String objectName = target.errors.objectName
        Errors childErrors = value.errors
        List<FieldError> childFieldErrors = childErrors.fieldErrors

        childFieldErrors.each { FieldError childFieldError ->
            String field

            if (index != null) {
                field = "${propertyName}.${index}.${childFieldError.field}"
            } else {
                field = "${propertyName}.${childFieldError.field}"
            }

            FieldError fieldError = new FieldError(objectName, field, childFieldError.rejectedValue, childFieldError.bindingFailure, childFieldError.codes, childFieldError.arguments, childFieldError.defaultMessage)
            errors.addError(fieldError)
        }
    }

}