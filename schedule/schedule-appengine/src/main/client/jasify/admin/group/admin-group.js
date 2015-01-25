(function (angular) {
    "use strict";

    angular.module('jasifyWeb').controller('AdminGroupController', AdminGroupController);

    function AdminGroupController($log, $q, User, Group, group) {
        var vm = this;
        vm.alerts = [];
        vm.alert = alert;

        vm.group = group;
        vm.groupForm = {};
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

        function loadUsers() {
            Group.users(vm.group.id).then(ok, errorHandler);
            function ok(users) {
                vm.users = users.items;
            }
        }

        function addUser(user) {
            Group.addUser(vm.group, user).then(ok, errorHandler);
            function ok(r) {
                alert('info', 'User added');
                vm.user = null;
                vm.loadUsers();
            }
        }

        function removeUser(user) {
            Group.removeUser(vm.group, user).then(ok, errorHandler);
            function ok(r) {
                var count = angular.isArray(r) ? r.length : 1;
                vm.alert('info', 'Removed ' + count + ' user(s)');
                vm.selectedUsers = [];
                vm.loadUsers();
            }
        }

        function save() {
            Group.update(vm.group).then(ok, errorHandler);
            function ok(o) {
                vm.group = o;
                vm.groupForm.$setPristine();
                vm.alert('info', 'Group saved.');
            }
        }

        function reset() {
            Group.get(vm.group.id).then(ok, errorHandler);

            vm.group = {};

            function ok(o) {
                vm.group = o;
                vm.groupForm.$setPristine();
                vm.alert('info', 'Form reset.');
            }
        }

        function errorHandler(resp) {
            alert('danger', 'Operation failed: (' + resp.status + ") '" + resp.statusText + "'");
            return $q.reject();
        }

    }

})(angular);