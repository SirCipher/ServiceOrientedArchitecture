import {ACCESS_TOKEN, API_BASE_URL} from '../constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
        .then(response => {
            var json = response.json();

            // If a new JWT has been provided then refresh the stored one
            if (response.headers) {
                var header = response.headers.get('Authorization');
                if (header) {
                    localStorage.setItem(ACCESS_TOKEN, header);
                }
            }

            if (!response.ok) {
                return json.then(Promise.reject.bind(Promise));
            }

            return json;
        });
};

export function getNote(id) {
    return request({
        url: API_BASE_URL + "/notes/query?id=" + id,
        method: 'GET'
    });
}

export function saveNote(note) {
    return request({
        url: API_BASE_URL + "/notes/query",
        method: 'POST',
        body: JSON.stringify(note)
    });
}

export function createNote(note, id) {
    return request({
        url: API_BASE_URL + "/notes/query?parentId=" + id,
        method: 'PUT',
        body: JSON.stringify(note)
    });
}

export function createNotebook(name) {
    return request({
        url: API_BASE_URL + "/notebooks/query?name=" + name,
        method: 'PUT'
    });
}

export function starNote(id) {
    return request({
        url: API_BASE_URL + "/notes/star?id=" + id,
        method: 'POST'
    });
}

export function deleteNote(id) {
    return request({
        url: API_BASE_URL + "/notes/query?id=" + id,
        method: 'DELETE'
    });
}

export function deleteNotebook(id) {
    return request({
        url: API_BASE_URL + "/notebooks/query?id=" + id,
        method: 'DELETE'
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/authorisation/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function shareNotebook(email, access, notebook_id) {
    return request({
        url: API_BASE_URL + "/notebooks/share?email=" + email + "&access=" + access + "&notebook_id=" + notebook_id,
        method: 'POST'
    });
}

export function unshareNotebook(notebook_id) {
    return request({
        url: API_BASE_URL + "/notebooks/share?notebook_id=" + notebook_id,
        method: 'DELETE'
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/authorisation/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function usernameAvailable(username) {
    return request({
        url: API_BASE_URL + "/authorisation/usernameAvailable?username=" + username,
        method: 'GET'
    });
}

export function emailAvailable(email) {
    return request({
        url: API_BASE_URL + "/authorisation/emailAvailable?email=" + email,
        method: 'GET'
    });
}

export function getNotebooks() {
    return request({
        url: API_BASE_URL + "/notebooks/",
        method: 'GET'
    });
}

export function getCurrentUser() {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}

export function getUserProfile(username) {
    return request({
        url: API_BASE_URL + "/users/" + username,
        method: 'GET'
    });
}
