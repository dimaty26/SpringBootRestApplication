let modal = $('#defaultModal');
let modalTitle = $('.modal-title');
let modalBody = $('.modal-body');
let modalFooter = $('.modal-footer');

let primaryButton = $('<button type="button" class="btn btn-primary"></button>');
let dismissButton = $('<button type="button" class="btn btn-secondary" data-dismiss="modal"></button>');
let dangerButton = $('<button type="button" class="btn btn-danger"></button>');

$(document).ready(function () {
    viewAdminsTable();
    viewUserTable();
    defaultModal();
    validation();
});

// Example starter JavaScript for disabling form submissions if there are invalid fields
(function() {
    'use strict';
    window.addEventListener('load', function() {
        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        var forms = document.getElementsByClassName('needs-validation');
        // Loop over them and prevent submission
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('saveUserButton', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();

//Modal Form Validation
function validation() {
    $(modal).validate({
        rules: {
            firstName: "required",
            lastName: "required",
            email: {
                required: true,
                email: true
            }
        },
        messages: {
            firstName: "Please specify your first name",
            lastName: "Please specify your last name",
            email: {
                required: "Please provide your email address",
                email: "Your email address must be in the format of name@domain.com"
            }
        }
    });
};

function defaultModal() {
    modal.modal({
        keyboard: true,
        backdrop: "static",
        show: false,
    }).on("show.bs.modal", function (event) {
        let button = $(event.relatedTarget);
        let id = button.data('id');
        let action = button.data('action');
        switch (action) {
            case 'addUser':
                addUser($(this));
                break;

            case 'editUser':
                editUser($(this), id);
                break;

            case 'deleteUser':
                deleteUser($(this), id);
                break;
        }
    }).on('hidden.bs.modal', function (event) {
        $(this).find('.modal-title').html('');
        $(this).find('.modal-body').html('');
        $(this).find('.modal-footer').html('');
    })
}

async function viewUserTable() {
    $('#noAdminTable tbody').empty();
    const userResponse = await userService.findById();
    const userJson = userResponse.json();

    userJson.then(user => {
        console.log(user);
        let roles = []
        user.roles.forEach(role => roles.push(role.name))
        let userRow = `$(<tr>
                      <th scope="row">${user.id}</th>
                      <td>${user.firstName}</td>
                      <td>${user.lastName}</td>
                      <td>${user.email}</td>
                      <td>${roles}</td>
            </tr>)`;
        console.log(userRow);
        $('#noAdminTable tbody').append(userRow);
    });
}

async function viewAdminsTable() {
    $('#userTable tbody').empty();
    const usersResponse = await adminService.getUsers();
    const usersJson = usersResponse.json();

    usersJson.then(users => {
        users.forEach(user => {
            let roles = []
            user.roles.forEach(role => roles.push(role.name))
            let userRow = `$(<tr>
                        <th scope="row">${user.id}</th>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.email}</td>
                        <td>${roles}</td>
                        <td class="text-center">
                                <button class="btn btn-warning btn-sm" data-id="${user.id}" 
                                data-action="editUser" data-toggle="modal" data-target="#defaultModal">Edit</button>
                                
                                <button class="btn btn-danger btn-sm" data-id="${user.id}" data-action="deleteUser" 
                                data-toggle="modal" data-target="#defaultModal">Delete</button>
                        </td>
                    </tr>)`;
            $('#userTable tbody').append(userRow);
        });
    });
}

async function addUser(modal) {
    const rolesResponse = await roleService.findAll();
    const rolesJson = rolesResponse.json();

    modal.find(modalTitle).html('Add User');
    let userFormHidden = $('.userForm:hidden')[0];
    modal.find(modalBody).html($(userFormHidden).clone());
    let userForm = modal.find('.userForm');
    userForm.prop('id', 'userForm');
    modal.find(userForm).show();
    dismissButton.html('Cancel');
    modal.find(modalFooter).append(dismissButton);
    primaryButton.prop('id', 'saveUserButton');
    primaryButton.html('Save');
    modal.find(modalFooter).append(primaryButton);
    rolesJson.then(roles => {
        roles.forEach(role => {
            modal.find('#role1').append(new Option(role.name, role.id));
            modal.find('#role2').append(new Option(role.name, role.id));
        });
    });

    $('#saveUserButton').click(async function (event) {
        let firstName = userForm.find('#firstName').val().trim();
        let lastName = userForm.find('#lastName').val().trim();
        let email = userForm.find('#email').val().trim();
        let password = userForm.find('#password').val().trim();
        let roleId1 = null;
        if ($(userForm.find('#role1')).is(":checked")) {
            roleId1 = userForm.find('#role1').val();
        }
        let roleId2 = null;
        if ($(userForm.find('#role2')).is(":checked")) {
            roleId2 = userForm.find('#role2').val();
        }
        let data;
        if (roleId2 === null && roleId1 != null) {
            data = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                roles: [
                    {
                        id: roleId1
                    }
                ]
            }
        } else if (roleId1 === null && roleId2 != null) {
            data = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                roles: [
                    {
                        id: roleId2
                    }
                ]
            }
        } else if (roleId1 != null && roleId2 != null) {
            data = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                roles: [
                    {
                        id: roleId1
                    },
                    {
                        id: roleId2
                    }
                ]
            }
        }

        const userResponse = await adminService.addUser(data);

        if (userResponse.status === 201) {
            viewAdminsTable();
            modal.find('.modal-title').html('Success');
            modal.find('.modal-body').html('User added!');
            dismissButton.html('Close');
            modal.find(modalFooter).html(dismissButton);
            $('#defaultModal').modal('show');
        } else if (userResponse.status === 400) {
            userResponse.json().then(response => {
                response.errors.forEach(function (error) {
                    let field = error.split(":")[0];
                    let message = error.split(":")[1];
                    modal.find('#' + field).addClass('is-invalid');
                    modal.find('#' + field).next('.invalid-feedback').text(message);
                });
            });
        } else {
            userResponse.json().then(response => {
                console.log(response);
                let alert = `<div class="alert alert-success alert-dismissible fade show col-12" role="alert">
                    ${response.error}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>`;
                modal.find('.modal-body').prepend(alert);
            });
        }
    });
}

async function editUser(modal, id) {
    const userResponse = await adminService.findById(id);
    const userJson = userResponse.json();
    const roleResponse = await roleService.findAll();
    const roleJson = roleResponse.json();

    let idInput = `<div class="form-group">
        <label for="id">Id</label>
        <input type="text" class="form-control" id="id" name="id" disabled>
        <div class="invalid-feedback"></div>
        </div>`;

    modal.find(modalTitle).html('Edit User');
    let userFormHidden = $('.userForm:hidden')[0];
    modal.find(modalBody).html($(userFormHidden).clone());
    let userForm = modal.find('.userForm');
    userForm.prop('id', 'updateUserForm');
    modal.find(userForm).prepend(idInput);
    modal.find(userForm).show();
    dismissButton.html('Cancel');
    modal.find(modalFooter).append(dismissButton);
    primaryButton.prop('id', 'updateUserButton');
    primaryButton.html('Update');
    modal.find(modalFooter).append(primaryButton);

    userJson.then(user => {
        let rolesArr = []
        user.roles.forEach(role => rolesArr.push(role.id))
        console.log(user.firstName, user.email, rolesArr);
        modal.find('#id').val(user.id);
        modal.find('#firstName').val(user.firstName);
        modal.find('#lastName').val(user.lastName);
        modal.find('#email').val(user.email);
        modal.find('#password').val(user.password);
        roleJson.then(roles => {
            roles.forEach(role => {
                if (rolesArr.includes(role.id) && role.id === 1) {
                    console.log(user.firstName, user.email, roles, "first if");
                    modal.find('#role1').prop('checked', true);
                } else if (rolesArr.includes(role.id) && role.id === 2) {
                    console.log(user.firstName, user.email, roles, "second if");
                    modal.find('#role2').prop('checked', true);
                } else if (rolesArr.includes(role.id)) {
                    console.log(user.firstName, user.email, roles, "second if");
                    modal.find('#role2').prop('checked', true);
                }
            });
        });
    });

    $('#updateUserButton').click(async function (event) {
        let id = userForm.find('#id').val().trim();
        let firstName = userForm.find('#firstName').val().trim();
        let lastName = userForm.find('#lastName').val().trim();
        let email = userForm.find('#email').val().trim();
        let password = userForm.find('#password').val().trim();
        let roleId1 = null;
        if ($(userForm.find('#role1')).is(":checked")) {
            roleId1 = userForm.find('#role1').val();
        }
        let roleId2 = null;
        if ($(userForm.find('#role2')).is(":checked")) {
            roleId2 = userForm.find('#role2').val();
        }
        let data;
        if (roleId2 === null && roleId1 != null) {
            data = {
                id: id,
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                roles: [
                    {
                        id: roleId1
                    }
                ]
            }
        } else if (roleId1 === null && roleId2 != null) {
            data = {
                id: id,
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                roles: [
                    {
                        id: roleId2
                    }
                ]
            }
        } else if (roleId1 != null && roleId2 != null) {
            data = {
                id: id,
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                roles: [
                    {
                        id: roleId1
                    },
                    {
                        id: roleId2
                    }
                ]
            }
        }
        ;

        const userResponse = await adminService.updateUser(data);

        if (userResponse.status === 200) {
            viewAdminsTable();
            modal.find('.modal-title').html('Success');
            modal.find('.modal-body').html('User updated!');
            dismissButton.html('Close');
            modal.find(modalFooter).html(dismissButton);
            $('#defaultModal').modal('show');
        } else if (userResponse.status === 400) {
            userResponse.json().then(response => {
                console.log(response);
                response.validationErrors.forEach(function (error) {
                    modal.find('#' + error.field).addClass('is-invalid');
                    modal.find('#' + error.field).next('.invalid-feedback').text(error.message);
                });
            });
        } else {
            userResponse.json().then(response => {
                console.log(response);
                let alert = `<div class="alert alert-success alert-dismissible fade show col-12" role="alert">
                        ${response.error}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>`;
                modal.find('.modal-body').prepend(alert);
            });
        }
    });
}

async function deleteUser(modal, id) {
    const userResponse = await adminService.findById(id);
    const userJson = userResponse.json();

    modal.find(modalTitle).html('Delete User');
    let message = '<strong>Are you sure to delete this user?</strong>';
    modal.find(modalBody).html(message);
    let viewUserTableHidden = $('.viewUserTable:hidden')[0];
    modal.find(modalBody).append($(viewUserTableHidden).clone());
    let viewUserTable = modal.find('.viewUserTable');
    modal.find(viewUserTable).show();
    dismissButton.html('Close');
    modal.find(modalFooter).append(dismissButton);
    dangerButton.prop('id', 'deleteUserButton');
    dangerButton.html('Delete');
    modal.find(modalFooter).append(dangerButton);

    userJson.then(user => {
        modal.find('#id').html(user.id);
        modal.find('#firstName').html(user.firstName);
        modal.find('#lastName').html(user.lastName);
        modal.find('#email').html(user.email);
        modal.find('#password').html(user.password);
        modal.find('#role').html(user.roles);
    });

    $('#deleteUserButton').click(async function (event) {
        const userResponse = await adminService.delete(id);
        const userJson = userResponse.json();

        if (userResponse.status === 204) {
            viewAdminsTable();
            modal.find('.modal-title').html('Success');
            modal.find('.modal-body').html('User deleted!');
            dismissButton.html('Close');
            modal.find(modalFooter).html(dismissButton);
            $('#defaultModal').modal('show');
        } else {
            userJson.then(response => {
                let alert = `<div class="alert alert-success alert-dismissible fade show col-12" role="alert">
                            ${response.error}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
                modal.find('.modal-body').prepend(alert);
            });
        }
    });
}

const buttonSaveListener = document.getElementById('Save');



const http = {
    fetch: async function (url, options = {}) {
        const response = await fetch(url, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            ...options,
        });
        return response;
    }
};

const userService = {
    findById: async () => {
        return await http.fetch('/api/users');
    }
};

const adminService = {
    getUsers: async () => {
        return await http.fetch('/api/admin');
    },
    addUser: async (data) => {
        console.log(JSON.stringify(data));
        return await http.fetch('/api/admin', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    findById: async (id) => {
        return await http.fetch('/api/admin/' + id);
    },
    updateUser: async (data) => {
        return await http.fetch('/api/admin/update', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    delete: async (id) => {
        return await http.fetch('/api/admin/' + id, {
            method: 'DELETE'
        });
    },
};

const roleService = {
    findAll: async () => {
        return await http.fetch('/api/roles');
    }
}

