# Content Management and List System

#### A platform for managing content (movies, series, etc.) with TMDB API integration and List features.




## 📌 Features

- 🎥 Content Service: Fetch content details from TMDB API.
- 📚 Library Service: Organize content into lists, create new lists, add content to them, and manage your lists.
- 🪪 Authentication Service: Manages all account transactions. Registration, login, logout, etc.
- 🚪 Gateway Service: Handles API routing and load balancing.
- 🗂️ Eureka Server: Manages service discovery and registration.
- 💬 Comment Service: Allows users to comment on content.
- 📄 Content Page Service : Creates pages of content.






## 💻 Technologies Used

- Java 17+
- Spring Boot Framework
- Docker
- Spring Cloud Eureka for service discovery
- Spring Cloud Gateway for API routing
- PostgreSQL for database
- TMDB API for content details
- Feign Client for inter-service communication
- Redis for caching
- Spring Security for authentication and authorization
- Spring Data JPA for database interaction
- JWT (JSON Web Token) for session handlin

## 🧩 Microservices

### 🪪  Authentication Service

📌 Features
- 🔐 User Authentication: Secure user login and registration.
- 🔑 JWT Token Generation: Generates JWT tokens for session - management.
- 🛡️ Authorization Middleware: Validates requests based on user roles.
- 📂 User Management: Manage user details and permissions.
- 📑 Session Handling: Token expiration and refresh mechanisms.

### 🎬 Content Service

- 🌍 TMDB API Integration: Fetches movie, TV series, and documentary data from the TMDB API
- 📦 Content Fetching: Retrieves and serves specific content information from an external API.
- 🧠 Metadata Handling: Processes details of the content such as description, score, type.
- 🔄 Caching Support: Implements a caching mechanism for frequently used data
- 🔎 Search Endpoint: Filters and returns content according to user search.

### 📚 Library Service
- 📁 List Management: Create, delete, update user-specific content lists.
- ➕ Add/Remove Content: Adding/removing content to lists.
- 🎯 User-Scoped Data: It contains list and content relationships specific to each user.
- 📡 Feign Client Integration: Gets content details via Content Service.
- 🧹 Cache Eviction: Clears related cache data on list changes.

### 💬 Comment Service
- 💭 User Comments: Allows users to comment on content.
- 🧵 Threaded Replies: Supports replying to main comments.
- 🗑️ Soft Delete Support: Soft-deleted comments remain in the system and are marked as inactive.
- 📚 Content-Based Grouping: Comments are grouped by content.
- 🔐 Auth Check: Identity verification checks are performed during comment transactions.

### 🌐 Gateway Service
- 🚪 Central Entry Point: Provides a single access point for all services.
- 🧭 Route Management: Directs requests to the relevant services.
- 🛡️ Global Filters: Includes security and logging filters applied to all traffic.
- 🔗 Path Rewrite: Handles URL rewriting and redirection.

### 🧭 Eureka Server
- 📡 Service Discovery: It enables microservices to dynamically discover each other.
- 🗂️ Registry System: Keeps all services on record.
- 🔁 Load Balancing Ready: Provides infrastructure for future load balancing integration.
- 📶 Heartbeat Monitoring: Checks if the services are active.
- ⚠️ Failover Detection: Notifies the system when services are down.

### 📄 Content Page Service
- 🖼️ Detail View Renderer: Renders the detail page of a specific content.
- 📑 Dynamic Content Loading: Content details are served dynamically.
- 📎 Merge Content + Comments: Comments and information about the content are presented on the same page.
- 📱 User View Friendly: Provides a simplified JSON format for the frontend side.
- 🧲 Aggregated Data Source: Combines data from different services.

