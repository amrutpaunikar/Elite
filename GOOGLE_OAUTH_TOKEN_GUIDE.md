# How to Get JWT Token After Google OAuth Login

This guide explains how to retrieve the JWT token after logging in with Google OAuth.

## Overview

After a successful Google OAuth login, the system:
1. Creates or finds the user in the database
2. Generates a JWT token
3. Stores the token in the session
4. Redirects to `/dashboard` with the token in the URL

## Methods to Get the Token

### Method 1: Extract Token from Redirect URL (Recommended for Frontend)

After Google OAuth login, you'll be redirected to:
```
/dashboard?token=<YOUR_JWT_TOKEN>
```

**Frontend Example (JavaScript/React):**
```javascript
// After redirect to /dashboard?token=...
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');

if (token) {
    // Store token in localStorage or state
    localStorage.setItem('authToken', token);
    console.log('Token received:', token);
}
```

### Method 2: Call the Token Endpoint (Alternative)

After OAuth login, call the `/oauth/token` endpoint to retrieve the token from the session.

**Request:**
```http
GET /oauth/token
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "user_id_here",
    "email": "user@example.com"
}
```

**Example using fetch:**
```javascript
fetch('http://localhost:8080/oauth/token', {
    method: 'GET',
    credentials: 'include' // Important: include cookies/session
})
.then(response => response.json())
.then(data => {
    const token = data.token;
    localStorage.setItem('authToken', token);
    console.log('Token:', token);
});
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/oauth/token \
  --cookie-jar cookies.txt \
  --cookie cookies.txt
```

## Complete OAuth Flow

### Step 1: Initiate Google Login
Navigate to or redirect to:
```
GET /googlelogin
```
This redirects to Google OAuth consent screen.

### Step 2: After Google Authentication
- User authenticates with Google
- System creates/finds user in database
- JWT token is generated
- Redirect happens to `/dashboard?token=<token>`

### Step 3: Use the Token
Include the token in subsequent API requests:

**Using Authorization Header:**
```javascript
fetch('http://localhost:8080/dashboard', {
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    }
});
```

**Using cURL:**
```bash
curl -X GET http://localhost:8080/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Logout

To clear the OAuth session:
```http
GET /oauth/logout
```

This clears the session and invalidates the stored token.

## Important Notes

1. **Token Expiration**: The JWT token expires after the time specified in `app.jwtExpirationMs` (default: 8640000ms = 24 hours)

2. **Session Storage**: The token is stored in the HTTP session, so:
   - Use `credentials: 'include'` in fetch requests
   - Ensure cookies are enabled
   - Session persists until logout or expiration

3. **Security**: 
   - Never expose tokens in logs or client-side code unnecessarily
   - Use HTTPS in production
   - Store tokens securely (localStorage, httpOnly cookies, or secure state management)

4. **User Creation**: If a user logs in with Google for the first time, a new user record is automatically created in the database with their email.

## Testing the Flow

1. Start your Spring Boot application
2. Navigate to: `http://localhost:8080/googlelogin`
3. Complete Google OAuth authentication
4. You'll be redirected to `/dashboard?token=<your_token>`
5. Extract the token from the URL or call `/oauth/token`

## Troubleshooting

**Issue**: Token not found in `/oauth/token` endpoint
- **Solution**: Ensure you've completed the Google OAuth flow first. The token is only available after successful OAuth login.

**Issue**: Token missing from redirect URL
- **Solution**: Check server logs for errors. Ensure `OAuthSuccessHandler` is properly configured and `JwtUtils` is working.

**Issue**: Session not persisting
- **Solution**: Ensure cookies are enabled and `credentials: 'include'` is set in fetch requests. Check CORS configuration allows credentials.





