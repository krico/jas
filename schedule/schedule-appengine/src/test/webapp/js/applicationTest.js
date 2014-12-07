describe("Application tests", function () {

    beforeEach(module('jasifyScheduleApp'));

    describe('Session', function () {

        it("must pass", function () {
            expect("yes").toEqual("NO");
        });

    });
});