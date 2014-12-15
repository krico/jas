- Auth.login now received cred and returns post
- login must return session info
- Need to init and check if user is logged in
- username directive check
- User.query now calls get(/user);
- Auth.isAdmin and NavController
- test directives
- not-authorized.html should login

```
            $httpBackend.expectPOST('/auth/login', credentials)
            $httpBackend.expectPOST('/auth/change-password', {
            $httpBackend.expectGET('/auth/restore')
            $httpBackend.expectGET('/auth/restore').respond(401);
            $httpBackend.expectGET('/auth/restore')
            $httpBackend.expectGET('/auth/restore').respond(401);
            $httpBackend.expectPOST('/username', 'good').respond(200);
            $httpBackend.expectPOST('/username', 'bad-name').respond(406);
            $httpBackend.expectGET('/user/555').respond(200, {id: 555, name: 'user', email: 'user@jasify.com'});
            $httpBackend.expectGET('/user/555').respond(403 /* forbidden */);
            $httpBackend.expectGET('/user/555').respond(200, {id: 555, name: 'user', email: 'user@jasify.com'});
            $httpBackend.expectPOST('/user/555', {id: 555, name: 'user', email: 'user2@jasify.com'})
            $httpBackend.expectGET('/user').respond(200, [{id: 555, name: 'user', email: 'user@jasify.com'}]);
```