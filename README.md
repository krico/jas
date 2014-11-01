jas
===

Janes Asmussen Szarmach

Getting Started
===============

1. [Setup git](https://help.github.com/articles/set-up-git/)!
  * I suggest using ssh so [setup ssh keys](https://help.github.com/articles/generating-ssh-keys/).
  * Or get the GitHub native app for [mac](https://mac.github.com/) or [windows](https://windows.github.com/)
  but this will not integrate with your IDE...
2. Checkout the project
  * `git clone git@github.com:krico/jas.git`
3. Import the project in IntelliJ (**todo**: document eclipse)
  * Open IntelliJ
  * Select import
  * navigate to "./jas"
  * double click on pom.xml
  * That's it! You should see something like this ![multi module example](https://github.com/krico/jas/blob/master/images/multi-module.png)


Change log
==========

- I followed the [app engine tutorial](https://cloud.google.com/appengine/docs/java/gettingstarted/introduction) and checked in the [app-engine-tutorial](sandbox/app-engine-tutorial/), the resulting module of following that tutorial step by step. The app is [in production here](https://krico-test.appspot.com).
- I followed the [cloud endpoints tutorial](https://cloud.google.com/appengine/docs/java/endpoints/getstarted/backend/) and checked in the [cloud-endpoints-tutorial](sandbox/cloud-endpoints-tutorial/), the resulting module of following that tutorial step by step.
- I added a parent pom.xml so that you can import the entire project at once...

