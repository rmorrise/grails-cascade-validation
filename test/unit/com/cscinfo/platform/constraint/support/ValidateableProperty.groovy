package com.cscinfo.platform.constraint.support

import grails.validation.Validateable

/**
 * @author Eric Kelm
 */
@Validateable
class ValidateableProperty {
    String field

    static constraints = {
        field blank: false
    }
}