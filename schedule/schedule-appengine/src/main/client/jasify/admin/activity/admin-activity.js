/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivityController', AdminActivityController);

    function AdminActivityController($scope, $location, $moment,
                                     jasDialogs, Activity, aButtonController, ActivityType,
                                     activity, organizations) {
        var vm = this;

        vm.saveBtn = aButtonController.createSave();
        vm.organizations = organizations.items;
        vm.loadActivityTypes = loadActivityTypes;
        vm.activityTypeChanged = activityTypeChanged;
        vm.activity = activity;

        initOrganization();
        initDates();

        vm.saveOrUpdate = saveOrUpdate;

        vm.repeatDetails = {
            repeatType: "No",
            repeatUntilType: "Date"
        };

        function loadActivityTypes(organization) {

            vm.activityTypes = [];

            if (!organization || !organization.id) {
                return;
            }

            ActivityType.query(organization).then(ok, fail);

            function ok(r) {
                vm.activityTypes = r.items;
                selectActivityType(vm.activityTypes, vm.activity);
            }

            function fail(r) {
                //vm.alert('danger', 'Failed to load activity types');
            }
        }

        function saveOrUpdate() {

            var activityToSave = angular.copy(vm.activity),
                promise;

            activityToSave.start = $moment(activityToSave.start)
                .set('hour', vm.fromTime.hour)
                .set('minute', vm.fromTime.minute)
                .format();

            activityToSave.finish = $moment(activityToSave.finish)
                .set('hour', vm.toTime.hour)
                .set('minute', vm.toTime.minute)
                .format();

            if (activityToSave.start > activityToSave.finish) {
                jasDialogs.warning("Activity's finish date precedes start date. Please correct.");
                return;
            }

            if ($moment(activityToSave.start) < $moment()) {
                jasDialogs.warning("Activity's start date precedes current date. Please correct.");
                return;
            }

            if (activityToSave.id) {
                promise = Activity.update(activityToSave);
            } else {
                promise = Activity.add(activityToSave, vm.repeatDetails);
            }

            vm.saveBtn.start(promise);
            promise.then(ok, fail);

            function ok(result) {
                if (activityToSave.id) {
                    vm.activity = result;

                    vm.fromTime = {
                        hour: $moment(vm.activity.start).get('hour'),
                        minute: $moment(vm.activity.start).get('minute')
                    };
                    vm.toTime = {
                        hour: $moment(vm.activity.finish).get('hour'),
                        minute: $moment(vm.activity.finish).get('minute')
                    };

                } else {

                    $location.search({});

                    if (result.items.length === 1) {
                        jasDialogs.success('Activity was created.');
                        $location.path('/admin/activity/' + result.items[0].id);
                    } else if (result.items.length > 1) {
                        jasDialogs.success(result.items.length + ' activities were created.');
                        $location.path("/admin/activities/" + result.items[0].activityType.organizationId);
                    } else {
                        jasDialogs.warning('No activities were created.');
                        $location.path("/admin/activities");
                    }
                }
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
            }
        }

        function selectActivityType(activityTypes, activity) {
            if (vm.activityTypes.length === 1) {
                vm.activity.activityType = vm.activityTypes[0];
                vm.activityTypeChanged();
            } else if (activity.activityType && activity.activityType.id) {
                angular.forEach(activityTypes, function (value) {
                    if (activity.activityType.id === value.id) {
                        vm.activity.activityType = value;
                    }
                });
            }
        }

        function activityTypeChanged() {
            vm.activity.description = vm.activity.activityType.description;
            vm.activity.price = vm.activity.activityType.price;
            vm.activity.currency = vm.activity.activityType.currency;
            vm.activity.location = vm.activity.activityType.location;
            vm.activity.maxSubscriptions = vm.activity.activityType.maxSubscriptions;
        }

        function initOrganization() {

            if (!vm.organizations || vm.organizations.length === 0) {
                return;
            }

            if (activity.id) {
                vm.organization = _.find(
                    vm.organizations,
                    {id: vm.activity.activityType.organizationId}
                );
            } else {
                if (vm.organizations.length === 1) {
                    vm.organization = vm.organizations[0];
                } else {
                    vm.organization = _.find(vm.organizations, { id: $location.search().organizationId})
                }

                vm.loadActivityTypes(vm.organization);
            }
        }

        function initDates() {

            $scope.$watch('vm.activity.start', function () {
                vm.repeatUntilDateOptions.minDate =
                    vm.toDateOptions.minDate =
                        vm.activity.start;
            });

            vm.fromDateOptions = {
                minDate: $moment()
            };

            vm.toDateOptions = {
                minDate: $moment()
            };

            vm.repeatUntilDateOptions = {
                minDate: $moment()
            };

            if (!activity.id) {
                activity.start = $moment().add(1, 'day').add(1, 'hour').set('minute', 0).format();
                activity.finish = $moment().add(1, 'day').add(2, 'hour').set('minute', 0).format();
            }

            vm.fromTime = {
                hour: $moment(activity.start).get('hour'),
                minute: $moment(activity.start).get('minute')
            };
            vm.toTime = {
                hour: $moment(activity.finish).get('hour'),
                minute: $moment(activity.finish).get('minute')
            };
        }
    }

}(window.angular));