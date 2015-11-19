package com.cscinfo.platform.constraint.support

import grails.validation.Validateable

/**
 * @author Eric Kelm
 */
class ValidateableParentWithChildList implements Validateable {

    List children

    static hasMany = [children: ValidateableProperty]

    static constraints = {
        children cascade:true
    }
}