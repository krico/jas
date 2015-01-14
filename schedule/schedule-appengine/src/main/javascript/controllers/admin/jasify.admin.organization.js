(function (angular) {
    "use strict";

    angular.module('jasifyScheduleControllers').controller('AdminOrganizationController', AdminOrganizationController);

    function AdminOrganizationController($log, $q, User, Organization, organization) {
        var vm = this;
        vm.organization = organization;
        vm.alerts = [];
        vm.alert = alert;
        vm.reset = reset;
        vm.save = save;
        vm.searchUsers = searchUsers;
        vm.userFilter = userFilter;
        vm.displayUser = displayUser;
        vm.addUser = addUser;
        vm.removeUser = removeUser;
        vm.loadUsers = loadUsers;
        vm.organizationForm = {};
        vm.users = [];
        vm.user = null;
        vm.selectedUsers = [];
        vm.allUsers = null;
        vm.groups = [];

        vm.loadUsers();

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
            if (!user) return '';
            if (!user.id) return '';
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

        function loadUsers() {
            Organization.users(vm.organization.id).then(ok, errorHandler);
            function ok(users) {
                vm.users = users.items;
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