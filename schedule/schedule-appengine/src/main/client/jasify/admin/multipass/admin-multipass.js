/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminMultipassController', AdminMultipassController);

    function AdminMultipassController($location, $filter,
                                     jasDialogs, Multipass, aButtonController, ActivityType,
                                     multipass, organizations) {
        var vm = this;

        vm.saveBtn = aButtonController.createSave();
        vm.organizations = organizations.items;
        vm.loadActivityTypes = loadActivityTypes;
        vm.activityTypeChanged = activityTypeChanged;
        vm.saveOrUpdate = saveOrUpdate;
        vm.initOrganization = initOrganization;
        vm.addRule = addRule;
        vm.deleteRule = deleteRule;
        vm.activityTypes = [];
        vm.multipass = multipass;
        vm.rules = [{id: "Activity Types", name: "Activity Types", enabled:true},
            {id: "Expires", name: "Expires", enabled:true},
            {id: "Days", name: "Days", enabled:true},
            {id: "Time", name: "Time", enabled:true},
            {id: "Uses", name: "Uses", enabled:true}];
        vm.timeComparisonTypes = ['Before', 'After'];
        vm.days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

        var $translate = $filter('translate');

        vm.initOrganization();

        function loadActivityTypes(organization) {
            vm.activityTypes = [];

            if (!organization || !organization.id) {
                return;
            }

            ActivityType.query(organization).then(ok, fail);

            function ok(r) {
                vm.activityTypes = r.items;
                selectActivityType(vm.activityTypes, vm.multipass);
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }

        function saveOrUpdate() {
            var multipassToSave = angular.copy(vm.multipass),
                promise;

            if (multipassToSave.id) {
                promise = Multipass.update(multipassToSave);
            } else {
                promise = Multipass.add(vm.organization, multipassToSave);
            }

            vm.saveBtn.start(promise);
            promise.then(ok, fail);

            function ok(result) {
                if (multipassToSave.id) {
                    vm.multipass = result;
                    var multipassUpdatedTranslation = $translate('MULTIPASS_UPDATED');
                    jasDialogs.success(multipassUpdatedTranslation);
                } else {
                    $location.search({});

                    if (result !== null) {
                        var multipassCreatedTranslation = $translate('MULTIPASS_CREATED');
                        jasDialogs.success(multipassCreatedTranslation);
                        $location.path('/admin/multipass/' + result.id);
                    } else {
                        var noMultipassCreatedTranslation = $translate('MULTIPASS_NOT_CREATED');
                        jasDialogs.warning(noMultipassCreatedTranslation);
                        $location.path("/admin/multipasses");
                    }
                }
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }

        function selectActivityType(activityTypes, multipass) {
            if (vm.activityTypes.length === 1) {
                vm.multipass.activityType = vm.activityTypes[0];
                vm.activityTypeChanged();
            } else if (multipass.activityType && multipass.activityType.id) {
                angular.forEach(activityTypes, function (value) {
                    if (multipass.activityType.id === value.id) {
                        vm.multipass.activityType = value;
                    }
                });
            }
        }

        function activityTypeChanged() {
            // TODO if anything
        }

        function initOrganization() {
            if (!vm.organizations || vm.organizations.length === 0) {
                return;
            }

            if (vm.multipass.id) {
                vm.organization = _.find(
                    vm.organizations,
                    {id: vm.multipass.organizationId}
                );
            } else {
                if (vm.organizations.length === 1) {
                    vm.organization = vm.organizations[0];
                } else {
                    vm.organization = _.find(vm.organizations, { id: $location.search().organizationId});
                }

                vm.loadActivityTypes(vm.organization);
            }
        }

        function addRule(rule) {
            rule.enabled = false;
            vm.multipass.ruleIds.push(rule.id);
        }

        function deleteRule(id) {
            var i = vm.multipass.ruleIds.indexOf(id);
            vm.multipass.ruleIds.splice(i, 1);
            for (i = 0; i < vm.rules.length; ++i) {
                if (vm.rules[i].id == id) {
                    vm.rules[i].enabled = true;
                    break;
                }
            }
        }
    }

}(window.angular));