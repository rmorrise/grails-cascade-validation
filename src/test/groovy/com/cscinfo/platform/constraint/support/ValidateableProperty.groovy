package com.cscinfo.platform.constraint.support

import grails.validation.Validateable

/**
 * @author Eric Kelm
 */
class ValidateableProperty implements Validateable {
    String field

    static constraints = {
        field blank: false
    }
}