package com.cscinfo.platform.constraint.support

import grails.validation.Validateable

/**
 * @author Eric Kelm
 */
class ValidateableParent implements Validateable {

    ValidateableProperty property

    static constraints = {
        property cascade: true
    }
}