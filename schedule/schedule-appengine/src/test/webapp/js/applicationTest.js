describe("Application", function () {

    beforeEach(module('jasifyScheduleApp'));

    describe('Session', function () {
        var Session;
        beforeEach(inject(function (_Session_) {
            Session = _Session_;
        }));

        it("Should be null after instantiation", function () {

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);

        });

        it("Should keep the values of create", function () {

            Session.create(123, 555);

            expect(Session.id).toBe(123);
            expect(Session.userId).toBe(555);

            Session.destroy();

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);
        });

        it("Should be null after destroy", function () {

            Session.create(123, 555);
            Session.destroy();

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);
        });

    });
});