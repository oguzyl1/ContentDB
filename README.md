# Content Management and List System

---
A platform for managing content (movies, series, etc.) with TMDB API integration and List features.

## 📌 Features

- 🎥 **Content Service**: Fetch content details from TMDB API.
- 📚 **Library Service**: Organize content into lists, create new lists, add content to them, and manage your lists.
- 🪪 **Authentication Service**: Manages all account transactions. Registration, login, logout, etc.
- 🚪 **Gateway Service**: Handles API routing and load balancing.
- 🗂️ **Eureka Server**: Manages service discovery and registration.

## 💻 Technologies Used

- **Java 17+**
- **Spring Boot Framework**
- **Spring Cloud Eureka** for service discovery
- **Spring Cloud Gateway** for API routing
- **PostgreSQL** for database
- **TMDB API** for content details
- **Feign Client** for inter-service communication
- **Redis** for caching
- **Spring Security** for authentication and authorization
- **Spring Data JPA** for database interaction
- **JWT (JSON Web Token)** for session handling

# 🧩 Microservices

---

## 🪪 Authentication Service

The **Authentication Service** is responsible for user authentication, authorization, and token management. It provides
secure access to the system by handling user registrations, login mechanisms, and token validation.

### 📌 Features

- 🔐 **User Authentication**: Secure user login and registration.
- 🔑 **JWT Token Generation**: Generates JWT tokens for session management.
- 🛡️ **Authorization Middleware**: Validates requests based on user roles.
- 📂 **User Management**: Manage user details and permissions.
- 📑 **Session Handling**: Token expiration and refresh mechanisms.

### API Endpoints

###### 🏷️ Authentication Endpoints

| **Method** | **Endpoint**                        | **Description**                          |
|------------|-------------------------------------|------------------------------------------|
| POST       | `/api/auth/register`                | Register a new user                      |
| POST       | `/api/auth/login`                   | Authenticate user and generate JWT token |
| POST       | `/api/auth/password-reset/initiate` | Sends password reset request             |
| POST       | `/api/auth/password-reset/complete` | Complete password reset request          |

###### 👤 User Endpoints

| **Method** | **Endpoint**                            | **Description**                                         |
|------------|-----------------------------------------|---------------------------------------------------------|
| GET        | `/api/users/my-account/profile`         | Get the user their own information                      |
| PUT        | `/api/users/my-account/update`          | The user updates their own information                  |
| PUT        | `/api/users/my-account/change-password` | The user changes their own password                     |
| POST       | `/api/users/create-access`              | Access token is generated with the user's refresh token |
| DELETE     | `/api/users/my-account/delete`          | User deletes their own account                          |

###### 👮 Admin Endpoint

| **Method** | **Endpoint**               | **Description**                |
|------------|----------------------------|--------------------------------|
| GET        | `/api/admin/user/{userId}` | Get the user with User Id      |
| GET        | `/api/admin/user/all`      | Retrieves all undeleted users. |
|            |                            |                                |

## 🛠️ Security Mechanisms

1. **Password Hashing**: Uses BCrypt for secure password storage.
2. **JWT Token Authentication**: Tokens are used for user sessions.
3. **Role-Based Access Control**: Users have assigned roles such as `ADMIN` and `USER`.
4. **Token Expiration & Refresh**: Implements refresh token strategy to maintain user sessions.
5. **CORS Policy**: Configured to allow cross-origin requests securely.

## API Endpoints

### 🗂️ Content Service Endpoints

| **Endpoint**                       | **Method** | **Description**                                   |
|------------------------------------|------------|---------------------------------------------------|
| `/v1/content/imdbId?title={title}` | GET        | Fetch IMDb ID by content title (e.g. "Inception") |
| `/v1/content/poster?id={imdbId}`   | GET        | Fetch poster by IMDb ID                           |
| `/v1/content/details?id={imdbId}`  | GET        | Fetch content details by IMDb ID                  |
| `/v1/content/search?title={title}` | GET        | Search contents by title                          |

### 📚 Library Service Endpoints

| **Endpoint**                                  | **Method** | **Description**                   |
|-----------------------------------------------|------------|-----------------------------------|
| `/v1/userList/create`                         | POST       | Create a new userList             |
| `/v1/userList/getAll`                         | GET        | List all libraries                |
| `/v1/userList/{name}`                         | PUT        | Update userList name              |
| `/v1/userList/{name}`                         | DELETE     | Delete a userList                 |
| `/v1/userListContent/{libraryName}`           | PUT        | Add content to a userList         |
| `/v1/userListContent/contentID/{libraryName}` | GET        | Get content IDs from a userList   |
| `/v1/userListContent/{libraryName}`           | GET        | Get content cards from a userList |

---

## Example Requests

### Create a New Library

**Request:**

`POST /v1/userList/create`

**Body:**

```json
{
  "name": "MyLibrary"
}
```

### Create a New Library

**Request:**

`PUT /v1/userListContent/MyLibrary`

**Body:**

```json
{
  "title": "Inception"
}
```

---

## 🔮 Future Plans

- User authentication and authorization services
- AI-based content recommendations


