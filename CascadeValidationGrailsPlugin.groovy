import com.cscinfo.platform.constraint.CascadeValidationConstraint
import org.codehaus.groovy.grails.validation.ConstrainedProperty

class CascadeValidationGrailsPlugin {
    // the plugin version
    def version = "0.1.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Cascade Validation Plugin" // Headline display name of the plugin
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

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/cascade-validation"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Corporation Service Company", url: "http://www.cscinfo.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = []

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://svn2.cscinfo.com/csc/Platform/trunk/Apps/cascadeValidation_Java" ]

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithApplicationContext = { ctx ->
        // post initialization spring config
        ConstrainedProperty.registerNewConstraint(CascadeValidationConstraint.NAME, CascadeValidationConstraint)
    }
}
