// Function to handle the login form submission
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            // Build the request data
            const loginData = {
                email: email,
                password: password
            };
            
            // Send login request
            fetch('/auth/login-submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.token) {
                    // Save the token to localStorage
                    localStorage.setItem('jwt_token', data.token);
                    localStorage.setItem('user_email', data.email);
                    
                    // Redirect to profile page
                    window.location.href = '/profile';
                } else {
                    // Show error message
                    document.getElementById('errorMessage').textContent = data.error || 'Login failed';
                    document.getElementById('errorAlert').classList.remove('d-none');
                }
            })
            .catch(error => {
                console.error('Login error:', error);
                document.getElementById('errorMessage').textContent = 'An error occurred during login';
                document.getElementById('errorAlert').classList.remove('d-none');
            });
        });
    }
    
    // Check if user is logged in (has JWT token)
    const jwtToken = localStorage.getItem('jwt_token');
    
    // Add JWT token to all fetch requests
    const originalFetch = window.fetch;
    window.fetch = function(url, options = {}) {
        // Only add Authorization header if we have a token
        if (jwtToken) {
            options.headers = options.headers || {};
            // Don't override if Authorization is already set
            if (!options.headers.Authorization && !options.headers.authorization) {
                options.headers.Authorization = `Bearer ${jwtToken}`;
            }
        }
        
        return originalFetch(url, options);
    };
    
    // Add logout functionality
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Clear token from localStorage
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_email');
            
            // Redirect to login page
            window.location.href = '/auth/login?logout=true';
        });
    }
});
