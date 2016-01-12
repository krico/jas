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
        vm.saveOrUpdate = saveOrUpdate;
        vm.initOrganization = initOrganization;
        vm.loadActivityTypes = loadActivityTypes;
        vm.initFilters = initFilters;
        vm.addRule = addRule;
        vm.deleteRule = deleteRule;
        vm.init = init;
        vm.activityTypes = [];
        vm.multipass = multipass.data !== undefined ? multipass.data : {};
        vm.filters = {ruleIds:[]};
        vm.rules = [{id: "Activity Types", name: "Activity Types", enabled:false},
            {id: "Expires", name: "Expires", enabled:false},
            {id: "Days", name: "Days", enabled:false},
            {id: "Time", name: "Time", enabled:false},
            {id: "Uses", name: "Uses", enabled:false}];
        vm.timeComparisonTypes = ['Before', 'After'];
        vm.days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

        var $translate = $filter('translate');

        vm.init();

        function init() {
            if (vm.organizations && vm.organizations.length > 0) {
                vm.initOrganization();
            }
        }

        function initOrganization() {
            if (vm.multipass.id) {
                vm.organization = _.find(vm.organizations, {id: vm.multipass.organizationId});
            } else if (vm.organizations.length === 1) {
                vm.organization = vm.organizations[0];
            } else {
                vm.organization = _.find(vm.organizations, {id: $location.search().organizationId});
            }

            // Load the ActivityTypes for the selected organization
            vm.loadActivityTypes(vm.organization);
        }

        function loadActivityTypes(organization) {
            ActivityType.query(organization).then(ok, fail);

            function ok(r) {
                vm.activityTypes = r.items;
                // TODO: This smells. Should not init the filters in this method
                vm.initFilters();
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }

        function saveOrUpdate() {
            var multipassToSave = angular.copy(vm.multipass),
                promise;

            for (var i = 0; i < vm.rules.length; ++i) {
                if (vm.rules[i].enabled) {
                    if (vm.rules[i].id == "Activity Types") {
                        var activityTypeIds = [];
                        for (var at = 0; at < vm.filters.activityTypeFilter.length; at++) {
                            activityTypeIds.push(vm.filters.activityTypeFilter[at].id);
                        }
                        multipassToSave.activityTypeFilter = {activityTypeIds: activityTypeIds};
                    } else if (vm.rules[i].id == "Expires") {
                        multipassToSave.expiresAfter = vm.filters.expiresAfter;
                    } else if (vm.rules[i].id == "Days") {
                        multipassToSave.dayFilter = vm.filters.dayFilter;
                    } else if (vm.rules[i].id == "Time") {
                        multipassToSave.timeFilter = {comparisonType: vm.filters.timeFilter.comparisonType, hour: vm.filters.timeFilter.time.hour, minute: vm.filters.timeFilter.time.minute};
                    } else if (vm.rules[i].id == "Uses") {
                        multipassToSave.uses = vm.filters.uses;
                    }
                }
            }

            if (multipassToSave.id) {
                promise = Multipass.update(multipassToSave);
            } else {
                promise = Multipass.add(vm.organization, multipassToSave);
            }

            vm.saveBtn.start(promise);
            promise.then(ok, fail);

            function ok(result) {
                if (multipassToSave.id) {
                    vm.multipass = result.data;
                    var multipassUpdatedTranslation = $translate('MULTIPASS_UPDATED');
                    jasDialogs.success(multipassUpdatedTranslation);
                } else {
                    $location.search({});

                    if (result !== null) {
                        var multipassCreatedTranslation = $translate('MULTIPASS_CREATED');
                        jasDialogs.success(multipassCreatedTranslation);
                        $location.path('/admin/multipass/' + result.data.id);
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

        function initFilters() {
            if (vm.multipass.id) {
                for (var i = 0; i < vm.rules.length; ++i) {
                    if (vm.rules[i].id == "Activity Types" && multipass.activityTypeFilter) {
                        vm.filters.activityTypeFilter = [];
                        for (var at = 0; at < multipass.activityTypeFilter.activityTypeIds.length; at++) {
                            var activityType = getActivityType(multipass.activityTypeFilter.activityTypeIds[at]);
                            if (activityType) {
                                vm.filters.activityTypeFilter.push(activityType);
                            }
                        }
                        multipass.activityTypeFilter = undefined;
                        if (vm.filters.activityTypeFilter.length > 0) {
                            addRule(vm.rules[i]);
                        }
                    } else if (vm.rules[i].id == "Expires" && multipass.expiresAfter) {
                        vm.filters.expiresAfter = multipass.expiresAfter;
                        multipass.expiresAfter = undefined;
                        addRule(vm.rules[i]);
                    } else if (vm.rules[i].id == "Days" && multipass.dayFilter) {
                        vm.filters.dayFilter = multipass.dayFilter;
                        multipass.dayFilter = undefined;
                        addRule(vm.rules[i]);
                    } else if (vm.rules[i].id == "Time" && multipass.timeFilter) {
                        vm.filters.timeFilter = {comparisonType: multipass.timeFilter.comparisonType};
                        vm.filters.timeFilter.time = {hour: multipass.timeFilter.hour, minute: multipass.timeFilter.minute};
                        multipass.timeFilter = undefined;
                        addRule(vm.rules[i]);
                    } else if (vm.rules[i].id == "Uses" && multipass.uses) {
                        vm.filters.uses = multipass.uses;
                        multipass.uses = undefined;
                        addRule(vm.rules[i]);
                    }
                }
            }
        }

        function getActivityType(activityTypeId) {
            for (var at = 0; at < vm.activityTypes.length; at++) {
                if (vm.activityTypes[at].id == activityTypeId) {
                    return vm.activityTypes[at];
                }
            }
            return undefined;
        }

        function addRule(rule) {
            rule.enabled = true;
            vm.filters.ruleIds.push(rule.id);
        }

        function deleteRule(id) {
            var i = vm.filters.ruleIds.indexOf(id);
            vm.filters.ruleIds.splice(i, 1);
            for (i = 0; i < vm.rules.length; ++i) {
                if (vm.rules[i].id == id) {
                    vm.rules[i].enabled = false;
                    break;
                }
            }
        }
    }

}(window.angular));