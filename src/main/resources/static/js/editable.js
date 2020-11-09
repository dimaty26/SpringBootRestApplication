let modal = $('#defaultModal');
let modalTitle = $('.modal-title');
let modalBody = $('.modal-body');
let modalFooter = $('.modal-footer');

let primaryButton = $('<button type="button" class="btn btn-primary"></button>');
let dismissButton = $('<button type="button" class="btn btn-secondary" data-dismiss="modal"></button>');
let dangerButton = $('<button type="button" class="btn btn-danger"></button>');

$(document).ready(function(){
    viewUsersTable();
    defaultModal();
});

function defaultModal() {
    modal.modal({
        keyboard: true,
        backdrop: "static",
        show: false,
    }).on("show.bs.modal", function(event) {
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
        }
    }).on('hidden.bs.modal', function(event) {
        $(this).find('.modal-title').html('');
        $(this).find('.modal-body').html('');
        $(this).find('.modal-footer').html('');
    })
}

async function viewUsersTable() {
    $('#userTable tbody').empty();
    const usersResponse = await userService.getUsers();
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
                            <div class="btn-group" role="group" aria-label="Action Buttons">
                                <button class="btn btn-warning btn-sm" data-id="${user.id}" data-action="editUser" data-toggle="modal" data-target="#defaultModal">Edit</button>
                                <button class="btn btn-danger btn-sm" data-id="${user.id}" data-action="deleteUser" data-toggle="modal" data-target="#defaultModal">Delete</button>
                            </div>
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
    let userFormHidden = $('.addUserForm:hidden')[0];
    modal.find(modalBody).html($(userFormHidden).clone());
    let userForm = modal.find('.addUserForm');
    userForm.prop('id', 'addUserForm');
    modal.find(userForm).show();
    dismissButton.html('Cancel');
    modal.find(modalFooter).append(dismissButton);
    primaryButton.prop('id', 'saveUserButton');
    primaryButton.html('Save');
    modal.find(modalFooter).append(primaryButton);
    rolesJson.then(roles => {
        roles.forEach(role => {
            modal.find('#role').append(new Option(role.name, role.id));
        });
    });

    $('#saveUserButton').click(async function(event){
        let firstName = userForm.find('#firstName').val().trim();
        let lastName = userForm.find('#lastName').val().trim();
        let email = userForm.find('#email').val().trim();
        let password = userForm.find('#password').val().trim();
        let roleId = userForm.find('#role option:selected').val().trim();
        let data = {
            firstName: firstName,
            lastName: lastName,
            email: email,
            password: password,
            role: {
                id: roleId
            }
        };

        const userResponse = await userService.addUser(data);

        if (userResponse.status == 201) {
            viewUsersTable();
            modal.find('.modal-title').html('Success');
            modal.find('.modal-body').html('User added!');
            dismissButton.html('Close');
            modal.find(modalFooter).html(dismissButton);
            $('#defaultModal').modal('show');
        } else if (userResponse.status == 400) {
            userResponse.json().then(response => {
                response.validationErrors.forEach(function(error){
                    modal.find('#' + error.field).addClass('is-invalid');
                    modal.find('#' + error.field).next('.invalid-feedback').text(error.message);
                });
            });
        } else {
            userResponse.json().then(response => {
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
    const userResponse = await userService.findById(id);
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
       modal.find('#id').val(user.id);
       modal.find('#firstName').val(user.firstName);
       modal.find('#lastName').val(user.lastName);
       modal.find('#email').val(user.email);
       modal.find('#password').val(user.password);
       roleJson.then(roles => {
           roles.forEach(role => {
               if (user.role.id == role.id)
                   modal.find('#role').append(new Option(role.name, role.id, false, true));
               else
                   modal.find('#role').append(new Option(role.name, role.id));
           });
       });
    });

    $('#updateUserButton').click(async function(event) {
        let id = userForm.find('#id').val().trim();
        let firstName = userForm.find('#firstName').val().trim();
        let lastName = userForm.find('#lastName').val().trim();
        let email = userForm.find('#email').val().trim();
        let password = userForm.find('#password').val().trim();
        let roleId = userForm.find('#role option:selected').val().trim();
        let data = {
            id: id,
            firstName: firstName,
            lastName: lastName,
            email: email,
            password: password,
            role: {
                id: roleId
            }
        };

        const userResponse = await userService.updateUser(id, data);

        if (userResponse.status == 200) {
            viewUsersTable();
            modal.find('.modal-title').html('Success');
            modal.find('.modal-body').html('User updated!');
            dismissButton.html('Close');
            modal.find(modalFooter).html(dismissButton);
            $('#defaultModal').modal('show');
        } else if (userResponse.status == 400) {
            userResponse.json().then(response => {
                response.validationErrors.forEach(function(error){
                    modal.find('#' + error.field).addClass('is-invalid');
                    modal.find('#' + error.field).next('.invalid-feedback').text(error.message);
                });
            });
        } else {
            userResponse.json().then(response => {
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
    const userResponse = await userService.findById(id);
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

    $('#deleteUserButton').click(async function(event){
        const userResponse = await userService.delete(id);

        if (userResponse.status == 204) {
            viewUsersTable();
            modal.find('.modal-title').html('Success');
            modal.find('.modal-body').html('User deleted!');
            dismissButton.html('Close');
            modal.find(modalFooter).html(dismissButton);
            $('#defaultModal').modal('show');
        } else {
            userResponse.json().then(response => {
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

const http = {
    fetch: async function(url, options = {}) {
        return await fetch(url, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            ...options,
        });
    }
};

const userService = {
    getUsers: async () => {
        return await http.fetch('/api/users');
    },
    addUser: async (data) => {
        return await http.fetch('/api/users', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    findById: async (id) => {
      return await http.fetch('/api/users/' + id);
    },
    updateUser: async (id, data) => {
        return await http.fetch('/api/users/' + id, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    delete: async (id) => {
        return await http.fetch('/api/users/' + id, {
            method: 'DELETE'
        });
    },
};

const roleService = {
    findAll: async () => {
        return await http.fetch('/api/roles');
    }
}

