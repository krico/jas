## 1.0-SNAPSHOT

Features:

  - mvn appengine:devserver works!

Changes:

  - created login.html
  - changed appengine:devserver to listen on 0.0.0.0 (pom.xml) so I can test templates from the phone.  To get the same
  in IntelliJ add `-a 0.0.0.0` as a server parameter.
  - installed Respond: `bower install respond -S`, needed for IE8 compatibility
  - installed html5shiv: `bower install html5shiv -S`, needed for IE8 compatibility
  - installed bootstrap (brings in jquery): `bower install bootstrap -S`
  - installed angularjs: `bower install angular -S`
  - configured bower (.bowerrc + bower.json), installed components go under webapp/bc
  - schedule-appengine module created