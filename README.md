# Video Downloader Service

A Spring Boot service designed to interact with upstream media and subscription APIs, handle cryptographic request signing, query subscriber channels, and extract media URLs with pagination and date filtering.

---

## Features

- **Automated Request Signing:** Implements dynamic HMAC/SHA-1 and checksum-based request signature generation via OpenFeign interceptors.
- **Subscription Management:** Fetches active subscription lists and channel profiles.
- **Media Extraction & Filtering:** Paginates through user posts, filters media by post age (`daysOld`), and outputs direct media URLs.
- **Data Archiving:** Exports complete raw JSON post details to disk for auditing and offline processing.
- **Hardened Production Packaging:** Includes an RPM profile with systemd sandboxing (`ProtectSystem=strict`, `NoNewPrivileges=true`, dedicated unprivileged service account).

---

## Tech Stack & Prerequisites

- **Java:** OpenJDK 21+
- **Framework:** Spring Boot 4.1.1
- **API Client:** Spring Cloud OpenFeign (2025.1.3)
- **JSON Processing:** Jackson 3 (`tools.jackson`)
- **Cryptography:** Apache Commons Codec 1.22.1
- **Code Style:** Google Java Format enforced via Spotless
- **Packaging:** RPM via `rpm-maven-plugin` (requires `rpm-build` on RHEL/CentOS/Rocky/AlmaLinux)

---

## Configuration

Configuration values are mapped through Spring Boot's `@ConfigurationProperties` and can be set in `src/main/resources/application.yaml`, an external configuration file, or environment variables.

### Configuration Properties

```yaml
server:
  port: 8080
  address: 127.0.0.1          # Bind to loopback by default for security

app:
  api:
    base-url: "https://api.example.com"

  config:
    user-id: "YOUR_USER_ID"
    user-agent: "Mozilla/5.0 (...)"
    x-bc-token: "YOUR_XBC_TOKEN"
    sess: "YOUR_SESSION_COOKIE"
    app-token: "YOUR_APP_TOKEN"

  rules:
    static-param: "STATIC_SALT_OR_KEY"
    checksum-indexes: [0, 5, 10]
    checksum-constant: 42
    format: "%s:%02x"
```

| Property | Description |
| :--- | :--- |
| `app.api.base-url` | Base URL of the upstream REST API. |
| `app.config.user-id` | Account identifier for the upstream user. |
| `app.config.user-agent`| User-Agent header string expected by upstream servers. |
| `app.config.x-bc-token`| Upstream device/client identification token (`x-bc`). |
| `app.config.sess` | Session cookie token passed in the `Cookie` header. |
| `app.config.app-token` | API application authorization token. |
| `app.rules.*` | Parameters used by `ApiAuthService` to sign requests. |

---

## REST API Reference

### 1. List Subscriptions
Retrieves active subscribed usernames.

- **URL:** `/api/subscriptions/all`
- **Method:** `GET`
- **Response:**
  ```json
  [
    "creator_alice",
    "creator_bob"
  ]
  ```

### 2. Fetch Media Posts
Retrieves the 5 most recent media posts for a given creator profile. Also exports the full retrieved post payloads to a local file (`posts_retrieval_<date>.txt`).

- **URL:** `/api/media/{profile}`
- **Method:** `GET`
- **Parameters:**
  - `profile` (Path, required): Target creator profile username or ID.
  - `daysOld` (Query, optional): Filter posts published within the last *N* days (e.g. `?daysOld=7`).
- **Response:**
  ```json
  [
    {
      "postedAt": "2026-10-04T07:15:30.000Z",
      "url": "https://cdn.example.com/media/video-full-1080p.mp4"
    }
  ]
  ```

---

## Development & Build

### Compile and Verify Code Style
This project enforces Google Java Style and sorted POM structure with Spotless:

```bash
# Check code style
mvn spotless:check

# Auto-format Java files and pom.xml
mvn spotless:apply
```

### Run Tests
```bash
mvn test
```

### Run Locally
```bash
# Build the JAR
mvn clean package

# Run with bundled defaults
java -jar target/video-downloader-0.0.1-SNAPSHOT.jar

# Run with an external configuration file
java -Dspring.config.additional-location=file:/path/to/custom-application.yaml -jar target/video-downloader-0.0.1-SNAPSHOT.jar
```

---

## Production Deployment (RPM & Systemd)

The project includes an RPM packaging profile targeted at enterprise Linux distributions (RHEL, Rocky Linux, AlmaLinux 8/9+).

### 1. Build RPM Package
Ensure `rpm-build` is installed on your build machine (`sudo dnf install -y rpm-build`):

```bash
mvn -Prpm -DskipTests package
```

The output package will be generated at:
```
target/rpm/video-downloader/RPMS/noarch/video-downloader-<version>.noarch.rpm
```

### 2. Package Layout

| Destination | Mode | Owner | Purpose |
| :--- | :--- | :--- | :--- |
| `/usr/share/video-downloader/video-downloader.jar` | `0644` | `root:root` | Executable Spring Boot JAR. |
| `/usr/lib/systemd/system/video-downloader.service` | `0644` | `root:root` | Systemd service definition. |
| `/etc/video-downloader/application.yaml` | `0640` | `root:video-downloader` | External configuration/secrets (`noreplace`). |
| `/etc/sysconfig/video-downloader` | `0640` | `root:video-downloader` | JVM options & environment file (`noreplace`). |
| `/var/lib/video-downloader` | `0750` | `video-downloader:video-downloader` | Working directory (saved post archives). |
| `/var/log/video-downloader` | `0750` | `video-downloader:video-downloader` | Dedicated logging directory. |

### 3. Install & Start Service

```bash
# Install package
sudo rpm -ivh target/rpm/video-downloader/RPMS/noarch/video-downloader-*.noarch.rpm

# Populate your secrets in /etc/video-downloader/application.yaml
sudo vi /etc/video-downloader/application.yaml

# Enable and start the service
sudo systemctl enable --now video-downloader.service

# Check service status
sudo systemctl status video-downloader

# Inspect service logs
sudo journalctl -u video-downloader -f
```

---

## Security Considerations

- **Secret Protection:** `/etc/video-downloader/application.yaml` is permission-restricted (`0640`, group `video-downloader`) so unprivileged users cannot read stored API tokens.
- **Service Isolation:** The service runs as a dedicated system user (`video-downloader`) with `ProtectSystem=strict`, `ProtectHome=true`, `NoNewPrivileges=true`, and writable paths restricted exclusively to `/var/lib/video-downloader` and `/var/log/video-downloader`.
- **Network Exposure:** Endpoints currently lack authentication. Keep `server.address` configured to `127.0.0.1` unless behind an authenticated reverse proxy (such as Nginx or Envoy).
