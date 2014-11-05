## 1.0-SNAPSHOT

Features:

  - mvn appengine:devserver works!

Changes:

  - installed angular-resource: `bower install angular-resource -S` to provide higher level access to $http
  - installed angular-spinner: `bower install angular-spinner -S` so we can show a spinner while loading stuff, spin.js was brought in
  - installed angular-mocks: `bower install angular-mocks -S` to allow us to mock the backend
  - installed angular-strap: `bower install angular-strap -S` which brought in angular-motion and angular-animate
  - installed angular-route: `bower install angular-route -S`
  - created login.html
  - changed appengine:devserver to listen on 0.0.0.0 (pom.xml) so I can test templates from the phone.  To get the same
  in IntelliJ add `-a 0.0.0.0` as a server parameter.
  - installed Respond: `bower install respond -S`, needed for IE8 compatibility
  - installed html5shiv: `bower install html5shiv -S`, needed for IE8 compatibility
  - installed bootstrap (brings in jquery): `bower install bootstrap -S`
  - installed angularjs: `bower install angular -S`
  - configured bower (.bowerrc + bower.json), installed components go under webapp/bc
  - schedule-appengine module created