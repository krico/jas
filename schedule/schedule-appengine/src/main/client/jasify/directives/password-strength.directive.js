(function (angular) {
    /**
     * Password strength meter
     */
    angular.module('jasify').directive('jasPasswordStrength', jasPasswordStrength);

    function jasPasswordStrength($log) {
        return {
            replace: true,
            restrict: 'E' /* A - attribute name, E - element name, C - classname */,
            //require: 'ngModel',
            scope: {
                password: '=password'
            },
            link: link,
            templateUrl: 'directives/password-strength.directive.html'
        };

        function link(scope, elm, attrs, ctrl) {

            scope.strength = pwStrength;
            scope.style = style;
            scope.css = css;

            function style(p) {
                return {width: scope.strength(p) + '%'};
            }

            function css(p) {
                var s = scope.strength(p);
                if (s <= 15) {
                    return ['progress-bar', 'progress-bar-danger'];
                } else if (s <= 40) {
                    return ['progress-bar', 'progress-bar-warning'];
                } else {
                    return ['progress-bar', 'progress-bar-success'];
                }
            }

            /*
             * Algorithm that determines pw strength
             * Based on https://github.com/subarroca/ng-password-strength/blob/master/app/scripts/directives/ng-password-strength.js
             * but with no dependency on underscorejs...
             */
            function pwStrength(pwField) {
                var p = pwField.$viewValue;

                if (!p) return -1;

                var criteria = {pos: {}, neg: {}};
                var points = {pos: {}, neg: {seqLetter: 0, seqNumber: 0, seqSymbol: 0}};
                var tmp,
                    strength = 0,
                    letters = 'abcdefghijklmnopqrstuvwxyz',
                    numbers = '01234567890',
                    symbols = '\\!@#$%&/()=?¿',
                    back,
                    forth,
                    i;

                // Benefits
                criteria.pos.lower = p.match(/[a-z]/g);
                criteria.pos.upper = p.match(/[A-Z]/g);
                criteria.pos.numbers = p.match(/\d/g);
                criteria.pos.symbols = p.match(/[$-/:-?{-~!^_`\[\]]/g);
                criteria.pos.middleNumber = p.slice(1, -1).match(/\d/g);
                criteria.pos.middleSymbol = p.slice(1, -1).match(/[$-/:-?{-~!^_`\[\]]/g);

                points.pos.lower = criteria.pos.lower ? criteria.pos.lower.length : 0;
                points.pos.upper = criteria.pos.upper ? criteria.pos.upper.length : 0;
                points.pos.numbers = criteria.pos.numbers ? criteria.pos.numbers.length : 0;
                points.pos.symbols = criteria.pos.symbols ? criteria.pos.symbols.length : 0;

                var ctx = {points: 0};
                angular.forEach(points.pos, function (value, key) {
                    this.points += Math.min(1, value);
                }, ctx);

                tmp = ctx.points;
                points.pos.numChars = p.length;
                tmp += (points.pos.numChars >= 8) ? 1 : 0;

                points.pos.requirements = (tmp >= 3) ? tmp : 0;
                points.pos.middleNumber = criteria.pos.middleNumber ? criteria.pos.middleNumber.length : 0;
                points.pos.middleSymbol = criteria.pos.middleSymbol ? criteria.pos.middleSymbol.length : 0;

                // Deductions
                criteria.neg.consecLower = p.match(/(?=([a-z]{2}))/g);
                criteria.neg.consecUpper = p.match(/(?=([A-Z]{2}))/g);
                criteria.neg.consecNumbers = p.match(/(?=(\d{2}))/g);
                criteria.neg.onlyNumbers = p.match(/^[0-9]*$/g);
                criteria.neg.onlyLetters = p.match(/^([a-z]|[A-Z])*$/g);

                points.neg.consecLower = criteria.neg.consecLower ? criteria.neg.consecLower.length : 0;
                points.neg.consecUpper = criteria.neg.consecUpper ? criteria.neg.consecUpper.length : 0;
                points.neg.consecNumbers = criteria.neg.consecNumbers ? criteria.neg.consecNumbers.length : 0;

                var reverse = function (input) {
                    var result = "";
                    input = input || "";
                    for (var i = 0; i < input.length; i++) {
                        result = input.charAt(i) + result;
                    }
                    return result;
                };

                // sequential letters (back and forth)
                for (i = 0; i < letters.length - 2; i++) {
                    var p2 = p.toLowerCase();
                    forth = letters.substring(i, parseInt(i + 3));
                    back = reverse(forth);
                    if (p2.indexOf(forth) !== -1 || p2.indexOf(back) !== -1) {
                        points.neg.seqLetter++;
                    }
                }

                // sequential numbers (back and forth)
                for (i = 0; i < numbers.length - 2; i++) {
                    forth = numbers.substring(i, parseInt(i + 3));
                    back = reverse(forth);
                    if (p.indexOf(forth) !== -1 || p.toLowerCase().indexOf(back) !== -1) {
                        points.neg.seqNumber++;
                    }
                }

                // sequential symbols (back and forth)
                for (i = 0; i < symbols.length - 2; i++) {
                    forth = symbols.substring(i, parseInt(i + 3));
                    back = reverse(forth);
                    if (p.indexOf(forth) !== -1 || p.toLowerCase().indexOf(back) !== -1) {
                        points.neg.seqSymbol++;
                    }
                }

                // repeated chars
                var counts = {};
                angular.forEach(p.toLowerCase().split(''), function (v, k) {
                    if (!this[k]) {
                        this[k] = 1;
                    } else {
                        this[k]++;
                    }
                }, counts);

                var total = {count: 0};
                angular.forEach(counts, function (v, k) {
                    if (v > 1) this.count += v;
                }, total);

                points.neg.repeated = total.count;


                // Calculations
                strength += points.pos.numChars * 4;
                if (points.pos.upper) {
                    strength += (points.pos.numChars - points.pos.upper) * 2;
                }
                if (points.pos.lower) {
                    strength += (points.pos.numChars - points.pos.lower) * 2;
                }
                if (points.pos.upper || points.pos.lower) {
                    strength += points.pos.numbers * 4;
                }
                strength += points.pos.symbols * 6;
                strength += (points.pos.middleSymbol + points.pos.middleNumber) * 2;
                strength += points.pos.requirements * 2;

                strength -= points.neg.consecLower * 2;
                strength -= points.neg.consecUpper * 2;
                strength -= points.neg.consecNumbers * 2;
                strength -= points.neg.seqNumber * 3;
                strength -= points.neg.seqLetter * 3;
                strength -= points.neg.seqSymbol * 3;

                if (criteria.neg.onlyNumbers) {
                    strength -= points.pos.numChars;
                }
                if (criteria.neg.onlyLetters) {
                    strength -= points.pos.numChars;
                }
                if (points.neg.repeated) {
                    strength -= (points.neg.repeated / points.pos.numChars) * 10;
                }

                return Math.max(5, Math.min(100, Math.round(strength)));
            }

        }
    }
})(angular);