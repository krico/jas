describe('Popup', function () {
    var Popup, $interval, $windowMock;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$interval_, _Popup_, _$windowMock_) {
        $interval = _$interval_;
        Popup = _Popup_;
        $windowMock = _$windowMock_;
    }));

    it('build options with no args', function () {
        var opts = Popup.getOptions();
        expect(opts.width).toEqual(500);
        expect(opts.height).toEqual(500);
    });

    it('build options with args', function () {
        var opts = Popup.getOptions({width: 600});
        expect(opts.width).toEqual(600);
        expect(opts.height).toEqual(500);
    });

    it('build a string with options', function () {
        var str = Popup.optionsString({foo: 'bar', baz: 90});
        expect(str).toEqual('foo=bar,baz=90');
    });

    it('opens a popup window for auth', function () {
        var ret = {};
        var param = {x: null, y: null, z: null};
        $windowMock.open = function (x, y, z) {
            param.x = x;
            param.y = y;
            param.z = z;
            return ret;
        };
        var ok = null;
        var fail = null;
        Popup.open('testUrl', 'Facebook')
            .then(
            function () {
                ok = true;
            },
            function () {
                fail = true;
            });
        expect(param.x).toEqual('testUrl');
        expect(param.y).toEqual('_blank');
        expect(param.z.indexOf('height=269')).not.toEqual(-1);

        ret.closed = true;

        $interval.flush(60);

        expect(ok).toBe(null);
        expect(fail).toBe(true);
    });

});
