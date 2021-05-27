grails-cascade-validation
=========================

See: [https://github.com/rmorrise/grails-cascade-validation/wiki/How-to-use-cascade-validation].

This plugin establishes a 'cascade' constraint property for validateable objects. If "cascade:true" is set on a nested object, the nested object's validate() method will be invoked and the results will be reported as part of the parent object's validation.

To use this plugin, add the add the plugin to `build.gradle`:

dependencies {
     //CSC custom plugin for 'cascade' constraint
     compile "org.grails.plugins:cascade-validation:3.0.1"
}
```

Here is an example of a command object that uses the plugin:
```groovy
 @Validateable
 class PhoneNumber {
     long id
     String countryCode
     String areaCode
     String number
     String extension
     TelephoneType telephoneType
     boolean isPrimary

     static constraints = {
         areaCode(blank: false)
         number(blank: false)
         telephoneType(cascade: true)
     }

     @Validateable
     static class TelephoneType {
         String id
         boolean countryCodeRecommended

         static constraints = {
             id(blank: false)
             countryCodeRecommended(nullable: false)
         }
     }
 }
```
When the cascade: constraint is added on the telephoneType property, this enables nested validation. When the phoneNumber.validate() method is called, the telephoneType.validate() method will also be invoked. Field errors that are added to the telephoneType will also be added to the parent phoneNumber object.

This plugin was originally based on a blog post by Eric Kelm and is used here with Eric's permission.

NOTE:

When running a unit test, the cascade constraint isn't registered with Grails. To work around this issue, the test class must implement
`GrailsUnitTest` and the following code must be added to the `setup()` method of the test:

```groovy
     def setup() {
        CascadeConstraintRegistration.register(applicationContext)
     }
```
This will register the `CascadeConstraint` the same way as the plugin does at runtime.
