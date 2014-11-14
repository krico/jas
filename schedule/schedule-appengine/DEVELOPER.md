# Developer Notes

Notes for developers working on schedule-appengine...

## Model (Slim3)

 * If you get problems missing classes named *Meta (e.g. UserMeta) you need to run `mvn apt:process`
 * If you change any class annotated with @Model (usually under com.jasify.schedule.appengine.model.users) you **MUST**
 run `mvn apt:process`

## Links

 * [angular-spinner](https://github.com/urish/angular-spinner) and [spin.js](http://fgnass.github.io/spin.js/#!)
 * [Bootstrap CSS](http://getbootstrap.com/css/#overview)
 * [E2E HTTP Backend Mock](https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend) for backend less development
 * [Angular Service](https://docs.angularjs.org/guide/services#creating-services)
 * [Angular Forms](https://docs.angularjs.org/guide/forms)
 * [HTTP status codes](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)
 * [Angular locale support](https://docs.angularjs.org/guide/i18n)
 * [AppEngine java stuff](https://cloud.google.com/appengine/docs/java/)
 * [Slim3](https://sites.google.com/site/slim3appengine/) our data store framework
 * [Unit Testing Google Cloud](https://cloud.google.com/appengine/docs/java/tools/localunittesting) unit tests for AppEngine
 * [ServletUnit](http://httpunit.sourceforge.net/doc/servletunit-intro.html) part of HttpUnit to unit test servlets
 * [Boostrap columns of same height](http://www.minimit.com/articles/solutions-tutorials/bootstrap-3-responsive-columns-of-same-height)