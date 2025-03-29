# Content Management and Library System

A platform for managing content (movies, series, etc.) with OMDb API integration and userList features.

## 📌 Features

- 🎥 **Content Service**: Fetch content details from OMDb API.
- 📚 **Library Service**: Organize content into libraries, create new libraries, and add content to them.
- 🚪 **Gateway Service**: Handles API routing and load balancing.
- 🗂️ **Eureka Server**: Manages service discovery and registration.

## 💻 Technologies Used

- **Java 17+**
- **Spring Boot Framework**
- **Spring Cloud Eureka** for service discovery
- **Spring Cloud Gateway** for API routing
- **PostgreSQL** for database
- **OMDb API** for content details
- **Feign Client** for inter-service communication

## API Endpoints

### 🗂️ Content Service Endpoints

| **Endpoint**                       | **Method** | **Description**                                   |
|------------------------------------|------------|---------------------------------------------------|
| `/v1/content/imdbId?title={title}` | GET        | Fetch IMDb ID by content title (e.g. "Inception") |
| `/v1/content/poster?id={imdbId}`   | GET        | Fetch poster by IMDb ID                           |
| `/v1/content/details?id={imdbId}`  | GET        | Fetch content details by IMDb ID                  |
| `/v1/content/search?title={title}` | GET        | Search contents by title                          |

### 📚 Library Service Endpoints

| **Endpoint**                                 | **Method** | **Description**                  |
|----------------------------------------------|------------|----------------------------------|
| `/v1/userList/create`                         | POST       | Create a new userList             |
| `/v1/userList/getAll`                         | GET        | List all libraries               |
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


