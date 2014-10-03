import com.cscinfo.platform.constraint.CascadeValidationConstraint
import org.codehaus.groovy.grails.validation.ConstrainedProperty

class CascadeValidationGrailsPlugin {
    def version = "0.1.1"
    def grailsVersion = "2.3 > *"
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
    def documentation = "http://grails.org/plugin/cascade-validation"
    def license = "APACHE"
    def organization = [ name: "Corporation Service Company", url: "http://www.cscinfo.com/" ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/rmorrise/grails-cascade-validation/issues']
    def scm = [url: 'https://github.com/rmorrise/grails-cascade-validation']

    def doWithApplicationContext = { ctx ->
        ConstrainedProperty.registerNewConstraint(CascadeValidationConstraint.NAME, CascadeValidationConstraint)
    }
}
