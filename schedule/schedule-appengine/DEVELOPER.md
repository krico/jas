# Developer Notes

Notes for developers working on schedule-appengine...

## Getting started

 * Install [Node.js](http://nodejs.org/)
 * Install [npm](https://npmjs.com): `npm install npm -g`
 * Install [Gulp](http://gulpjs.com/): `npm install -g gulp`
 * Install [Karma](http://karma-runner.github.io/):
 ```
 npm install -g karma
 npm install -g karma-cli
 ```

### Running gulp

On the command line you should be able to run
```
cd jas/schedule/schedule-appengine
gulp
```

### Running karma (javascript tests)

Also on the command line, **with gulp running***:

```
cd jas/schedule/schedule-appengine
karma start src/test/javascript/karma.conf.js
```

### Running from IntelliJ (or eclipse)

You can run most of the tasks from intelliJ

#### Running Gulp on IntelliJ

* Run -> Edit Configurations
* Click on the "+" sign
* Select Gulp.js
* Name: gulp (jasify), Tasks: empty, Gulp Package: <JAS_ROOT>/schedule/schedule-appengine/node_modules/gulp

#### Running Karma on IntelliJ

* Plugins
* Browser repository
* Search for karma
* Install and restart

Now add a new run configuration
* Run -> Edit Configurations
* Click on the "+" sign
* Select Karma
* Name: karma (jasify), Configuration file: <JAS_ROOT>/schedule/schedule-appengine/src/test/javascript/karma.conf.js, Karma package: /Users/krico/Projects/jas/schedule/schedule-appengine/node_modules/karma


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
## Style

 * You [MUST read this](https://github.com/johnpapa/angularjs-styleguide)
 
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

## Protractor (end to end testing)

 * To get protractor going, have a look at the [tutorial](http://angular.github.io/protractor/#/tutorial).
 If you don't want to install anything and rely only on jasify, you can do
```
# download selenium binaries
./node_modules/.bin/webdriver-manager update
# start selenium server (http://localhost:4444/wd/hub)
./node_modules/.bin/webdriver-manager start

# on another terminal, run
./node_modules/.bin/protractor src/test/e2e/protractor.cfg.js
```
 * I've also setup a run configuration on intellij, for that you need to install the "NodeJS" plugin. Then it's quite
 simple:
   1. Edit Configurations -> Click on the "+" and choose run type "Node.js"
   2. Name: Jasify Protractor
   3. Working Dir: PATH_TO_CHECKOUT/schedule/schedule-appengine
   4. Javascript file: node_modules/protractor/lib/cli.js
   5. Application: src/test/e2e/protractor.cfg.js
   

## Links

 * [PayPal WebCheckout dev](https://developer.paypal.com/docs/integration/web/web-checkout/)
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


## GitHub Maven plugins

 * Edit ~/.m2/settings.xml (to find out the real path, run `mvn -X` and check the first few lines to see where your settings come from.
 * To use the GitHub plugins, add this to the settings.xml
```xml
<servers>
  <server>
    <id>github</id>
    <password>OAUTH2TOKEN</password>
  </server>
</servers>
```
