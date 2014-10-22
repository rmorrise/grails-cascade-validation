package com.cscinfo.platform.constraint.support

import grails.validation.Validateable

/**
 * @author Eric Kelm
 */
@Validateable
class ValidateableParent {

    ValidateableProperty property

    static constraints = {
        property cascade: true
    }
}