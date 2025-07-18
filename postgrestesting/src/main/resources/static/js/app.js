const API_BASE_URL = 'http://localhost:8000/apis';

function showMessage(elementId, message = '', isError = true) {
    const element = document.getElementById(elementId);
    if (!element) return;

    element.textContent = message;
    element.classList.remove('hidden');
    element.classList.toggle('text-red-500', isError);
    element.classList.toggle('text-green-500', !isError);
}

function checkAuth() {
    const token = localStorage.getItem('token');
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.classList.toggle('hidden', !token);
    }
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = '/login.html';
}

async function loadProfile() {
    const profileDetails = document.getElementById('profileDetails');
    const messageBox = document.getElementById('profileMessage');

    try {
        const response = await fetch(`${API_BASE_URL}/users/retrieve`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        });

        const user = await response.json();
        console.log("Retrieved User Profile:", user);

        if (response.ok) {
            profileDetails.innerHTML = `
                <p><strong>User ID:</strong> ${user.id}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Date of Birth:</strong> ${user.dob}</p>
                <p><strong>Profession:</strong> ${user.profession}</p>
                <p><strong>Mobile Number:</strong> ${user.mobileNumber}</p>
            `;

            document.getElementById('email').value = user.email;
            document.getElementById('dob').value = user.dob;
            document.getElementById('profession').value = user.profession;
            document.getElementById('mobile_number').value = user.mobileNumber;

            messageBox.classList.add('hidden');
        } else {
            showMessage('profileMessage', user.message || 'Failed to load profile');
        }
    } catch (error) {
        showMessage('profileMessage', 'Server error. Please try again later.');
    }
}

async function loadUrls() {
    const urlList = document.getElementById('urlList');
    if (!urlList) return;

    try {
        const response = await fetch(`${API_BASE_URL}/urls/retrieve-url-by-user-id`, {
            headers: { 'token': localStorage.getItem('token') }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to load URLs');
        }

        const data = await response.json();
        urlList.innerHTML = '';

        data.urls.forEach(url => {
            const urlDiv = document.createElement('div');
            urlDiv.className = 'bg-gray-900 text-white p-4 rounded-lg shadow-md';

            urlDiv.innerHTML = `
                <p><strong>Name:</strong> ${url.urlname}</p>
                <p><strong>Category:</strong> ${url.urlcategory}</p>
                <p><strong>Description:</strong> ${url.urldescription}</p>
                <p><strong>Link:</strong>
                    <a href="${url.urllink}" target="_blank" class="text-blue-400 hover:underline">
                        ${url.urllink}
                    </a>
                </p>
                <div class="flex gap-2 mt-3">
                    <button onclick="populateEditForm(${url.urlId})"
                        class="bg-yellow-600 text-white px-3 py-1 rounded hover:bg-yellow-700">
                        ✏️ Edit
                    </button>
                    <button onclick="deleteUrl(${url.urlId})"
                        class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">
                        🗑️ Delete
                    </button>
                </div>
            `;

            urlList.appendChild(urlDiv);
        });

    } catch (error) {
        console.error('loadUrls error:', error);
        urlList.innerHTML = `<p class="text-red-500">Failed to load URLs</p>`;
    }
}

async function deleteUrl(urlId) {
    const confirmed = confirm("Are you sure you want to delete this URL?");
    if (!confirmed) return;

    try {
        const response = await fetch(`${API_BASE_URL}/urls/delete/${urlId}`, {
            method: 'DELETE',
            headers: {
                'token': localStorage.getItem('token')
            }
        });

        const result = await response.json();

        if (response.ok) {
            alert(result.message || "URL deleted successfully!");
            await loadUrls(); // Refresh the list
        } else {
            alert(result.message || "Failed to delete URL");
        }
    } catch (error) {
        console.error("Delete error:", error);
        alert("Error deleting URL");
    }
}

document.addEventListener('DOMContentLoaded', () => {
    console.log("DOM fully loaded");
    checkAuth();

    const currentPage = window.location.pathname;

    // Toggle Add URL Form
    const showAddFormBtn = document.getElementById('showAddFormBtn');
    const addUrlFormContainer = document.getElementById('addUrlFormContainer');
    const cancelAddFormBtn = document.getElementById('cancelAddFormBtn');

    if (showAddFormBtn && addUrlFormContainer) {
        showAddFormBtn.addEventListener('click', () => {
            addUrlFormContainer.classList.remove('hidden');
            showAddFormBtn.classList.add('hidden');
        });
    }

    if (cancelAddFormBtn && addUrlFormContainer) {
        cancelAddFormBtn.addEventListener('click', () => {
            addUrlFormContainer.classList.add('hidden');
            showAddFormBtn.classList.remove('hidden');
            document.getElementById('urlForm').reset();
        });
    }

    if (currentPage.includes("urls.html")) {
        loadUrls();
    }

    if (currentPage.includes("profile.html")) {
        loadProfile();

        const updateBtn = document.getElementById('updateBtn');
        if (updateBtn) {
            updateBtn.addEventListener('click', () => {
                const updateFormContainer = document.getElementById('updateFormContainer');
                updateFormContainer?.classList.toggle('hidden');
            });
        }

        const updateForm = document.getElementById('updateProfileForm');
        if (updateForm) {
            updateForm.addEventListener('submit', async (e) => {
                e.preventDefault();

                const userData = {
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value,
                    dob: document.getElementById('dob').value,
                    profession: document.getElementById('profession').value,
                    mobileNumber: document.getElementById('mobile_number').value
                };

                try {
                    const response = await fetch(`${API_BASE_URL}/users/update`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'token': localStorage.getItem('token')
                        },
                        body: JSON.stringify(userData)
                    });

                    const result = await response.json();

                    if (response.ok) {
                        showMessage('updateMessage', result.message || 'Updated successfully!', false);
                        await loadProfile();
                        document.getElementById('updateFormContainer').classList.add('hidden');
                    } else {
                        showMessage('updateMessage', result.message || 'Failed to update profile');
                    }
                } catch (err) {
                    showMessage('updateMessage', 'Error connecting to server');
                }
            });
        }

        const deleteAccountBtn = document.getElementById('deleteAccountBtn');
        if (deleteAccountBtn) {
            deleteAccountBtn.addEventListener('click', async () => {
                if (!confirm('Are you sure you want to delete your account?')) return;

                try {
                    const response = await fetch(`${API_BASE_URL}/users/delete`, {
                        method: 'DELETE',
                        headers: {
                            'token': localStorage.getItem('token')
                        }
                    });

                    if (response.ok) {
                        localStorage.removeItem('token');
                        showMessage('profileMessage', 'Account deleted successfully!', false);
                        setTimeout(() => window.location.href = '/login.html', 2000);
                    } else {
                        showMessage('profileMessage', 'Failed to delete account');
                    }
                } catch (error) {
                    showMessage('profileMessage', 'Server error');
                }
            });
        }
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const userData = {
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                dob: document.getElementById('dob').value,
                profession: document.getElementById('profession').value,
                mobileNumber: document.getElementById('mobilenumber').value
            };

            try {
                const response = await fetch(`${API_BASE_URL}/users/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(userData)
                });

                const result = await response.json();
                if (response.ok) {
                    showMessage('registerMessage', 'Registration successful! Please login.', false);
                    setTimeout(() => window.location.href = '/login.html', 2000);
                } else {
                    showMessage('registerMessage', result.message || 'Registration failed');
                }
            } catch (error) {
                showMessage('registerMessage', 'Error connecting to server');
            }
        });
    }

   const loginForm = document.getElementById('loginForm');
   if (loginForm) {
       loginForm.addEventListener('submit', async (e) => {
           e.preventDefault();

           const credentials = {
               email: document.getElementById('email').value,
               password: document.getElementById('password').value
           };

           try {
               const response = await fetch(`${API_BASE_URL}/users/login`, {
                   method: 'POST',
                   headers: { 'Content-Type': 'application/json' },
                   body: JSON.stringify(credentials)
               });

               const text = await response.text();
               let result;

               try {
                   result = JSON.parse(text); // ✅ safe parsing
               } catch (e) {
                   console.error("❌ Server returned non-JSON:", text);
                   showMessage('loginMessage', 'Server error (not JSON)');
                   return;
               }

               if (response.ok) {
                   localStorage.setItem('token', result.token);
                   showMessage('loginMessage', 'Login successful!', false);
                   setTimeout(() => window.location.href = '/urls.html', 2000);
               } else {
                   showMessage('loginMessage', result.message || 'Login failed');
               }
           } catch (error) {
               console.error("❌ Connection error:", error);
               showMessage('loginMessage', 'Error connecting to server');
           }
       });
   }

    const urlForm = document.getElementById('urlForm');
    if (urlForm) {
        urlForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const urlData = {
                url_name: document.getElementById('url_name').value,
                url_category: document.getElementById('url_category').value,
                url_description: document.getElementById('url_description').value,
                url_link: document.getElementById('url_link').value
            };

            try {
                const response = await fetch(`${API_BASE_URL}/urls/insert`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'token': localStorage.getItem('token')
                    },
                    body: JSON.stringify(urlData)
                });

                const result = await response.json();

                if (response.ok) {
                    showMessage('urlMessage', result.message || 'URL added successfully!', false);
                    urlForm.reset();

                    document.getElementById('addUrlFormContainer').classList.add('hidden');
                    document.getElementById('showAddFormBtn').classList.remove('hidden');

                    await loadUrls();

                    setTimeout(() => {
                        const msg = document.getElementById('urlMessage');
                        if (msg) {
                            msg.textContent = '';
                            msg.classList.add('hidden');
                        }
                    }, 3000);
                } else {
                    showMessage('urlMessage', result.message || 'Failed to add URL');
                }
            } catch (error) {
                showMessage('urlMessage', 'Server not reachable!');
            }
        });
    }
    const editUrlForm = document.getElementById('editUrlForm');
    if (editUrlForm) {
        editUrlForm.addEventListener('submit', async (e) => {
            e.preventDefault();

           const updateData = {
               urlId: Number(document.getElementById('edit_url_id').value), // ✅ Must be non-null
               urlname: document.getElementById('edit_url_name').value,
               urlcategory: document.getElementById('edit_url_category').value,
               urldescription: document.getElementById('edit_url_description').value,
               urllink: document.getElementById('edit_url_link').value
           };
           console.log("Sending update data:", updateData);
            try {
                const response = await fetch(`${API_BASE_URL}/urls/update`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'token': localStorage.getItem('token')
                    },
                    body: JSON.stringify(updateData)
                });

                const result = await response.json();
                if (response.ok) {
                    showMessage('editUrlMessage', result.message || 'URL updated successfully!', false);
                    editUrlForm.reset();
                    document.getElementById('editUrlFormContainer').classList.add('hidden');
                    await loadUrls(); // refresh the list
                } else {
                    showMessage('editUrlMessage', result.message || 'Failed to update URL');
                }
            } catch (error) {
                showMessage('editUrlMessage', 'Error connecting to server');
            }
        });

        const cancelEditFormBtn = document.getElementById('cancelEditFormBtn');
        if (cancelEditFormBtn) {
            cancelEditFormBtn.addEventListener('click', () => {
                editUrlForm.reset();
                document.getElementById('editUrlFormContainer').classList.add('hidden');
            });
        }
    }

});
// Global function to populate edit form
window.populateEditForm = async function (urlId) {
    try {
        const response = await fetch(`${API_BASE_URL}/urls/retrieve-url-by-id/${urlId}`, {
            headers: { 'token': localStorage.getItem('token') }
        });

        const data = await response.json();
        const url = data.url;

        console.log("✅ Full URL object from backend:", url);

        // ✅ Correct key used here
        document.getElementById('edit_url_id').value = url.urlid;

        document.getElementById('edit_url_name').value = url.urlname;
        document.getElementById('edit_url_category').value = url.urlcategory;
        document.getElementById('edit_url_description').value = url.urldescription;
        document.getElementById('edit_url_link').value = url.urllink;

        console.log("✅ Setting edit_url_id to:", url.urlid);

        document.getElementById('editUrlFormContainer').classList.remove('hidden');

    } catch (error) {
        console.error('❌ Error loading URL for edit:', error);
        showMessage('editUrlMessage', error.message || 'Unable to load URL data for editing');
    }
};
