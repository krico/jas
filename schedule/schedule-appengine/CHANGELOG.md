## 1.0-SNAPSHOT

Features:

  - mvn appengine:devserver works!

Changes:

  - installed Respond: `bower install respond -S`, needed for IE8 compatibility
  - installed html5shiv: `bower install html5shiv -S`, needed for IE8 compatibility
  - installed bootstrap (brings in jquery): `bower install bootstrap -S`
  - installed angularjs: `bower install angular -S`
  - configured bower (.bowerrc + bower.json), installed components go under webapp/bc
  - schedule-appengine module created