## VelocityTemplateEngine template search path

```
vte.render("foo.vm",...);
```

will search for a file named `foo.vm` in the same directory as this README.md file.

### Development cycle

This is how I've been implementing / migrating templates

 1. Created template file (e.g.: `templates/subscriber/PasswordRecovery.html.vm`) with initial layout
 2. Write rendering test method `com.jasify.schedule.appengine.template.TemplateNamesTest`
 3. Run the test, and point your browser to template output dir.  Check for test output
```
 Aug 24, 2015 3:44:39 PM com.jasify.schedule.appengine.template.TemplateNamesTest createTemplateDir
 INFO:

 	TEMPLATE DIR: /Users/krico/Projects/jas/schedule/schedule-appengine/target/rendered-templates
```
 4. Now get your template to a good state (change *.vm file, run test, reload browser, ...)

### E-mail templates

For e-mails, after getting template done, you should now integrate this
in `com.jasify.schedule.appengine.communication.Communicator`.  I've been trying to keep it as model-driven as possible.

Prefer
```java
void notifyOfWhatever(User user);
```
And NOT `void notifyOfWhatever(String toEmail, String userName, String version, ...);`

You can put these entities directly in the velocity context `context.put("user", user);` and then use `$user.Name` to
get the String returned by `User.getName()`.

A trick in velocity is writing `$!user.Name` so that, in case the value is null, you get an empty string rather than
having "$user.Name" string in your template.

I've also created some helper tools that are always present in the context.
You can find them in `com.jasify.schedule.appengine.communication.ApplicationContextImpl`.

At the time of this writing (Mon Aug 24 18:00:07 CEST 2015) we had:

 * "app" - `com.jasify.schedule.appengine.communication.ApplicationContext.App`
 ```velocity
 $app.Url will give you the url of the running instance e.g.: https://jasify-schedule.appspost.com
 $app.Logo will give you the logo url e.g.: <img src=$app.Logo
 ```
 * "kut" - KeyUtil
 ```velocity
 $kut.keyToString($user.Id) will show U1234
 ```
 * "sut" - StringUtil from commons-lang3
 ```velocity
 $sut.trimToEmpty($user.Name)
 #if( $sut.isBlank($user.Name) )
 No Name
 #end
 ```
 * "mut" - ModelUtil (this is where you most likely will add methods).  We keep helper methods needed by templates to
 interact with the model here.  Examples:
 ```velocity
 $mut.url($recovery) to generate the url used for a PasswordRecovery object
 ```

Once you've written your Communicator method, if you want to test the mail output, add `-Djasify.emailDebug=true` to your
appengine startup (see DEVELOPER.md for details).  Make the e-mail use case, then open it with the URL to the e-mail output
that is printed to the logs.

That's it for now