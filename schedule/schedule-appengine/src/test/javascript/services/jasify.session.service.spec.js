describe('Session', function () {
    var Session;

    beforeEach(module('jasify'));

    beforeEach(inject(function (_Session_) {
        Session = _Session_;
    }));

    it("should be null after instantiation", function () {

        expect(Session.id).toBe(null);
        expect(Session.userId).toBe(null);

    });

    it("should keep the values of create", function () {

        Session.create(123, 555);

        expect(Session.id).toBe(123);
        expect(Session.userId).toBe(555);

    });

    it("should be null after destroy", function () {

        Session.create(123, 555);
        Session.destroy();

        expect(Session.id).toBe(null);
        expect(Session.userId).toBe(null);

    });

});
