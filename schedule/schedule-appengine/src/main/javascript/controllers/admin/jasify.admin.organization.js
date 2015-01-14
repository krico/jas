(function (angular) {
    "use strict";

    angular.module('jasifyScheduleControllers').controller('AdminOrganizationController', AdminOrganizationController);

    function AdminOrganizationController($log, $q, User, Group, Organization, organization) {
        var vm = this;
        vm.alerts = [];
        vm.alert = alert;

        vm.organization = organization;
        vm.organizationForm = {};
        vm.reset = reset;
        vm.save = save;

        vm.searchUsers = searchUsers;
        vm.userFilter = userFilter;
        vm.displayUser = displayUser;
        vm.addUser = addUser;
        vm.removeUser = removeUser;
        vm.loadUsers = loadUsers;

        vm.allUsers = null;
        vm.users = [];
        vm.selectedUsers = [];
        vm.user = null;

        vm.searchGroups = searchGroups;
        vm.addGroup = addGroup;
        vm.removeGroup = removeGroup;
        vm.loadGroups = loadGroups;

        vm.allGroups = null;
        vm.groups = [];
        vm.selectedGroups = [];
        vm.group = null;

        vm.loadUsers();
        vm.loadGroups();

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function userFilter(user, viewValue) {
            if (user.name && user.name.indexOf(viewValue) != -1) {
                return true;
            }
            if (user.email && user.email.indexOf(viewValue) != -1) {
                return true;
            }
            if (user.realName && user.realName.toLowerCase().indexOf(viewValue.toLowerCase()) != -1) {
                return true;
            }
            return false;
        }

        function displayUser(user) {
            if (!user || !user.id) return '';
            var ret = '[' + user.numericId + '] ';
            if (user.name) {
                ret += user.name;
            } else if (user.email) {
                ret += user.email;
            } else if (user.realName) {
                ret += user.realName;
            }
            return ret;
        }

        function searchUsers(v) {
            if (vm.allUsers === null) {
                vm.allUsers = [];
                return User.query({limit: 0}).then(ok, errorHandler);
            } else {
                var ret = [];
                for (var i in vm.allUsers) {
                    if (vm.userFilter(vm.allUsers[i], v)) {
                        ret.push(vm.allUsers[i]);
                    }
                }
                return $q.when(ret);
            }

            function ok(res) {
                vm.allUsers = res.users;
                return vm.searchUsers(v);
            }
        }

        function searchGroups(v) {
            if (vm.allGroups === null) {
                vm.allGroups = [];
                return Group.query().then(ok, errorHandler);
            } else {
                var ret = [];
                for (var i in vm.allGroups) {
                    var group = vm.allGroups[i];
                    if (group.name && group.name.indexOf(v) != -1) {
                        ret.push(group);
                    }
                }
                return $q.when(ret);
            }

            function ok(res) {
                vm.allGroups = res.items;
                return vm.searchGroups(v);
            }
        }

        function loadUsers() {
            Organization.users(vm.organization.id).then(ok, errorHandler);
            function ok(users) {
                vm.users = users.items;
            }
        }

        function loadGroups() {
            Organization.groups(vm.organization.id).then(ok, errorHandler);
            function ok(groups) {
                vm.groups = groups.items;
            }
        }

        function addUser(user) {
            Organization.addUser(vm.organization, user).then(ok, errorHandler);
            function ok(r) {
                alert('info', 'User added');
                vm.user = null;
                vm.loadUsers();
            }
        }

        function removeUser(user) {
            Organization.removeUser(vm.organization, user).then(ok, errorHandler);
            function ok(r) {
                var count = angular.isArray(r) ? r.length : 1;
                vm.alert('info', 'Removed ' + count + ' user(s)');
                vm.selectedUsers = [];
                vm.loadUsers();
            }
        }

        function addGroup(group) {
            Organization.addGroup(vm.organization, group).then(ok, errorHandler);
            function ok(r) {
                alert('info', 'Group added');
                vm.group = null;
                vm.loadGroups();
            }
        }

        function removeGroup(group) {
            Organization.removeGroup(vm.organization, group).then(ok, errorHandler);
            function ok(r) {
                var count = angular.isArray(r) ? r.length : 1;
                vm.alert('info', 'Removed ' + count + ' group(s)');
                vm.selectedGroups = [];
                vm.loadGroups();
            }
        }


        function save() {
            Organization.update(vm.organization).then(ok, errorHandler);
            function ok(o) {
                vm.organization = o;
                vm.organizationForm.$setPristine();
                vm.alert('info', 'Organization saved.');
            }
        }

        function reset() {
            Organization.get(vm.organization.id).then(ok, errorHandler);

            vm.organization = {};

            function ok(o) {
                vm.organization = o;
                vm.organizationForm.$setPristine();
                vm.alert('info', 'Form reset.');
            }
        }

        function errorHandler(resp) {
            alert('danger', 'Operation failed: (' + resp.status + ") '" + resp.statusText + "'");
            return $q.reject();
        }

    }

})(angular);