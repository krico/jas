# Developer Notes

Notes for developers working on schedule-appengine...

## Create a local config

To run your dev env you need to create a file called jasify.json inside your user home.
It should look something like (*you need to replace REAL_CLIENT_ID and REAL_CLIENT_SECRET with, duh...*), but if you
are not using oauth with google, you can put any value there:

```javascript

{
  "ApplicationConfig" : {
    "OAuth2ProviderConfig.Google.ClientId" : "REAL_CLIENT_ID",
    "OAuth2ProviderConfig.Google.ClientSecret" : "REAL_CLIENT_SECRET"
  }
}


```

## Model (Slim3)

 * If you get problems missing classes named *Meta (e.g. UserMeta) you need to run `mvn apt:process`
 * If you change any class annotated with @Model (usually under com.jasify.schedule.appengine.model.users) you **MUST**
 run `mvn apt:process`

## Google Endpoints
 * [Javascript API help](https://developers.google.com/api-client-library/javascript/dev/dev_jscript)

## Jasmine (testing javscript)

 * To run only the jasmine tests, you need to run `mvn phantomjs:install jasmine:test`.  Another option is to run a webserver
   and run your tests in the browser.  Then you can simply reload the browser to test again.  For this, run `mvn jasmine:bdd`.
 * Checkout [Jasmine Documentation](http://jasmine.github.io/2.0/introduction.html)

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