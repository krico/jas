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

```
Token info: {
  "access_type" : "online",
  "audience" : "205399701546-0h55q7jogn0kp1mdebgvd0foahobkj6i.apps.googleusercontent.com",
  "email" : "krico@cwa.to",
  "expires_in" : 3595,
  "issued_to" : "205399701546-0h55q7jogn0kp1mdebgvd0foahobkj6i.apps.googleusercontent.com",
  "scope" : "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.me",
  "user_id" : "113701523626567860489",
  "verified_email" : true
}
User info: {
  "email" : "krico@cwa.to",
  "family_name" : "Asmussen",
  "gender" : "male",
  "given_name" : "Christian",
  "hd" : "cwa.to",
  "id" : "113701523626567860489",
  "link" : "https://plus.google.com/113701523626567860489",
  "name" : "Christian Asmussen",
  "picture" : "https://lh3.googleusercontent.com/-0fPNn_hjgyE/AAAAAAAAAAI/AAAAAAAAALY/zecPUz4G_pA/photo.jpg",
  "verified_email" : true
}
```