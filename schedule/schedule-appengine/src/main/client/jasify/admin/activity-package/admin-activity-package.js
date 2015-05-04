(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackageController', AdminActivityPackageController);

    function AdminActivityPackageController($log, $location, $q, ActivityPackage, Activity, organizations, activityPackage, activityPackageActivities) {
        var vm = this;
        vm.alerts = [];
        vm.organization = {};

        /* updated by the vm.reset() call */
        vm.activityPackage = {};
        /* updated by the vm.reset() call */
        vm.selectedActivities = [];

        vm.activities = [];
        vm.organizations = organizations.items;
        vm.alert = alert;
        vm.back = back;
        vm.selectOrganization = selectOrganization;
        vm.onOrganizationSelected = onOrganizationSelected;
        vm.deselectActivity = deselectActivity;
        vm.selectActivity = selectActivity;
        vm.filterSelected = filterSelected;
        vm.sortActivityArray = sortActivityArray;

        vm.update = update;
        vm.create = create;
        vm.reset = reset;

        vm.reset();

        vm.selectOrganization(vm.organizations, vm.activityPackage, $location.search().organizationId);

        function update() {

            var toAdd = [];
            angular.forEach(vm.selectedActivities, function (value) {
                var selectedId = value.id;

                var found = [];
                angular.forEach(activityPackageActivities, function (existingValue) {
                    if (selectedId == existingValue.id) {
                        //noinspection JSPotentiallyInvalidUsageOfThis
                        this.push(existingValue);
                    }
                }, found);

                if (found.length === 0) {
                    // selected but not in the original :-> needs to be added
                    this.push(value);
                }

            }, toAdd);

            var toRemove = [];
            angular.forEach(activityPackageActivities, function (existingValue) {
                var found = [];
                var existingId = existingValue.id;
                angular.forEach(vm.selectedActivities, function (selectedValue) {
                    if (existingId == selectedValue.id) {
                        //noinspection JSPotentiallyInvalidUsageOfThis
                        this.push(selectedValue);
                    }
                }, found);

                if (found.length === 0) {
                    // in the original but not in the selected :-> needs to be removed
                    this.push(existingValue);
                }

            }, toRemove);

            //TODO: I really don't like the complexity, but I just want to finish this
            //We should do this server-side and have a single call to the backend
            var promises = [];
            promises.push(ActivityPackage.update(vm.activityPackage));

            angular.forEach(toRemove, function (remove) {
                var promise = ActivityPackage.removeActivity(vm.activityPackage, remove);
                this.push(promise);
            }, promises);

            angular.forEach(toAdd, function (add) {
                var promise = ActivityPackage.addActivity(vm.activityPackage, add);
                this.push(promise);
            }, promises);

            return $q.all(promises).then(ok, fail);

            function ok(resp) {
                //update original state
                activityPackage = angular.copy(vm.activityPackage);
                activityPackageActivities = angular.copy(vm.selectedActivities);

                makePristine();
            }

            function fail(resp) {
                //TODO
                $log.debug('FAIL: ' + angular.toJson(resp));
            }
        }

        function create() {
            if (!(vm.selectedActivities && vm.selectedActivities.length > 0)) {
                vm.alert('warning', 'You must select at least one activity');
                return;
            }
            vm.activityPackage.organizationId = vm.organization.id;

            ActivityPackage.add(vm.activityPackage, vm.selectedActivities).then(ok, fail);

            function ok(resp) {
                $location.path('/admin/activity-package/' + resp.id);
            }

            function fail(resp) {
                //TODO:
                $log.debug('FAIL: ' + angular.toJson(resp));
            }
        }

        function reset() {
            if (activityPackage) {
                vm.activityPackage = angular.copy(activityPackage);
            } else {
                vm.activityPackage = {};
            }

            if (activityPackageActivities) {
                vm.selectedActivities = angular.copy(activityPackageActivities);
            } else {
                vm.selectedActivities = [];
            }
            makePristine();
        }

        function makeDirty() {
            if (vm.activityPackageForm) {
                vm.activityPackageForm.$setDirty();
            }
        }

        function makePristine() {
            if (vm.activityPackageForm) {
                vm.activityPackageForm.$setPristine();
            }
        }

        function sortActivityArray(array) {
            array.sort(cmp);

            function cmp(activity1, activity2) {
                var a = activity1.start || new Date();
                var b = activity2.start || new Date();
                return a > b ? 1 : a < b ? -1 : 0;

            }
        }

        function selectActivity(activity) {
            makeDirty();
            vm.selectedActivities.push(activity);
            vm.sortActivityArray(vm.selectedActivities);
        }

        function deselectActivity(activityId) {
            if (!activityId) return;

            makeDirty();

            var indices = [];
            vm.selectedActivities.forEach(function (value, index) {

                if (activityId == value.id) {
                    indices.push(index);
                }
            });
            indices.forEach(function (index) {
                vm.selectedActivities.splice(index, 1);
            });
        }

        function filterSelected(value, index) {
            for (var i = 0; i < vm.selectedActivities.length; ++i) {
                if (vm.selectedActivities[i].id == value.id) return false;
            }
            return true;
        }

        function selectOrganization(organizations, activityPackage, organizationId) {
            if (activityPackage && activityPackage.organizationId) {
                angular.forEach(organizations, function (value, key) {
                    if (activityPackage.organizationId == value.id) {
                        vm.organization = value;
                    }
                });
            } else if (organizationId) {
                angular.forEach(organizations, function (value, key) {
                    if (organizationId == value.id) {
                        vm.organization = value;
                    }
                });
            }
            if (vm.organization && vm.organization.id) {
                vm.onOrganizationSelected(vm.organization);
            }
        }

        function onOrganizationSelected(organization) {
            vm.activities = [];
            if (!(vm.activityPackage &&
                vm.activityPackage.organizationId &&
                vm.activityPackage.organizationId == organization.id)) {
                vm.selectedActivities = [];
            }
            if (organization.id) {
                Activity.query({organizationId: organization.id}).then(ok, failed);
            }

            function ok(resp) {
                vm.activities = resp.items;
                vm.sortActivityArray(vm.activities);
            }

            function failed(reason) {
                vm.alert('danger', 'Failed: ' + angular.toJson(reason));
            }
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function back() {
            var orgId = null;
            if (vm.activityPackage && vm.activityPackage.organizationId) {
                orgId = vm.activityPackage.organizationId;
            } else if (vm.organization.id) {
                orgId = vm.organization.id;
            }

            if (orgId === null) {
                $location.path('/admin/activity-packages');
            } else {
                $location.path('/admin/activity-packages/' + orgId); //TODO: path orgID to activity/X
            }
        }
    }

})(angular);