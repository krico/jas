/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivityController', AdminActivityController);

    function AdminActivityController($location, $moment,
                                     jasDialogs, Activity, aButtonController, ActivityType,
                                     activity, organizations) {
        var vm = this;

        vm.saveBtn = aButtonController.createSave();
        vm.organizations = organizations.items;
        vm.loadActivityTypes = loadActivityTypes;
        vm.activityTypeChanged = activityTypeChanged;

        vm.activity = activity;

        if (activity.id) {

            vm.defaultFromDate = $moment(activity.start);
            vm.defaultToDate = $moment(activity.finish);

            vm.organization = _.find(
                vm.organizations,
                {id: vm.activity.activityType.organizationId}
            );
        } else {
            vm.defaultFromDate = $moment().add(1, 'hour');
            vm.defaultToDate = $moment().add(2, 'hour');
            if (vm.organizations && vm.organizations.length === 2) {
                vm.organization = vm.organizations[0];
                vm.loadActivityTypes(vm.organization);
            }
        }

        vm.activity.fromDate = vm.defaultFromDate.format('DD/MM/YYYY');
        vm.activity.fromTime = vm.defaultFromDate.format('LT');

        vm.activity.toDate = vm.defaultToDate.format('DD/MM/YYYY');
        vm.activity.toTime = vm.defaultToDate.format('LT');

        vm.saveOrUpdate = saveOrUpdate;

        vm.repeatDetails = {
            repeatType: "No",
            repeatUntilType: "Date"
        };

        function loadActivityTypes(organization) {

            vm.activityTypes = [];

            if (!organization.id) {
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

            activityToSave.start = $moment(vm.activity.fromDate + " " + vm.activity.fromTime, "DD/MM/YYYY LT");
            activityToSave.finish = $moment(vm.activity.toDate + " " + vm.activity.toTime, "DD/MM/YYYY LT");

            delete activityToSave.fromDate;
            delete activityToSave.fromTime;

            delete activityToSave.toDate;
            delete activityToSave.toTime;

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
                    vm.activity.fromDate = $moment(vm.activity.start).format('DD/MM/YYYY');
                    vm.activity.fromTime = $moment(vm.activity.start).format('LT');
                    vm.activity.toDate = $moment(vm.activity.finish).format('DD/MM/YYYY');
                    vm.activity.toTime = $moment(vm.activity.finish).format('LT');
                } else {
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
    }

}(window.angular));