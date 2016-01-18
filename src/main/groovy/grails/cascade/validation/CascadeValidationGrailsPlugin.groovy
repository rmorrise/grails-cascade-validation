package grails.cascade.validation

import com.cscinfo.platform.constraint.CascadeValidationConstraint
import grails.plugins.Plugin
import org.grails.datastore.gorm.validation.constraints.factory.DefaultConstraintFactory

class CascadeValidationGrailsPlugin extends Plugin {

    def grailsVersion = "3.3.0 > *"
    def title = "Cascade Validation Plugin"
    def author = "Russell Morrisey"
    def authorEmail = "rmorrise@cscinfo.com"
    def description = '''\
Establishes a 'cascade' constraint property for validateable objects. If "cascade:true" is set
 on a nested object, the nested object's validate() method will be invoked and the results will
 be reported as part of the parent object's validation.

Based on a blog post by Eric Kelm:
 http://asoftwareguy.com/2013/07/01/grails-cascade-validation-for-pogos/
Used with permission.
'''
    def documentation = "https://github.com/rmorrise/grails-cascade-validation/wiki/How-to-use-cascade-validation"
    def license = "APACHE"
    def organization = [name: "CSC", url: "http://www.cscglobal.com/"]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/rmorrise/grails-cascade-validation/issues']
    def scm = [url: 'https://github.com/rmorrise/grails-cascade-validation']

    def developers = [ [ name: "Soeren Glasius", email: "soeren@glasius.dk" ], [ name: "Russell Morrisey", email: "russell.morrisey@cscglobal.com" ]]

    //Class<? extends Constraint> constraintClass, MessageSource messageSource, List<Class> targetTypes = [Object]
    Closure doWithSpring() {{ ->
        cascadeValidationConstraintFactory(DefaultConstraintFactory, CascadeValidationConstraint, null)
    }}
}
