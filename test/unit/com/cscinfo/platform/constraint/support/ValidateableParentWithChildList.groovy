package com.cscinfo.platform.constraint.support

import grails.validation.Validateable

/**
 * @author Eric Kelm
 */
@Validateable
class ValidateableParentWithChildList {

    List children

    static hasMany = [children: ValidateableProperty]

    static constraints = {
        children cascade:true
    }
}