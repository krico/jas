## 1.0-SNAPSHOT

Features:

  - SignUp view (backend-less), our first view!  To get it going we had to setup
    - routing + navbar
    - backend-less support (index.html?nobackend)
    - form validation + ngModel
    - bootstrap form validation states
    - User service based on angular-resource
    - Angular directives with custom validators
    - angular-strap (better connection bootstrap : angular)
    - spinner.js to show a spinner as we check the username
  - Navbar (detect login/logout and change)
  - Login view (backend-less)
  - Profile view (backend-less)
  - Logout (backend-less)
  - mvn appengine:devserver works!

Changes:

  - ngMessage `bower install angular-messages -S`
  - upgraded BootstrapUI to 0.12.0
  - uninstalled angular-strap `bower uninstall angular-strap -S` (conflicts with BootstrapUI)...
  - BootstrapUI installed (for pagination): `bower install angular-bootstrap -S`
  - application.js contains core functionality, namely
    - jasifyScheduleApp - the main application
    - routing configuration
    - Service definitions (Modal - to display modal dialogs, Auth - for authentication/authorization ,
      Util - the name says it all, User - CRUD operations on user)
    - directives (strongPassword, confirmField)
  - controllers.js contains all controllers (one per view + one for the navbar)
  - backend-less.js has the necessary code to mock our backend and to allow development of the frontend independently.
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