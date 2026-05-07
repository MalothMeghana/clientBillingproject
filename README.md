# Client Billing Management System

A full-stack client billing application for managing clients, employees, team leads, projects, invoices, payments, authentication, profile data, email OTPs, and Razorpay payment verification.

The project contains:

- `backend/` - Spring Boot REST API
- `frontend/` - React + Vite user interface


## Features

- Admin login with JWT authentication
- Client login and protected client dashboard
- Client, employee, team lead, project, invoice, and payment management
- Invoice PDF generation/download support
- Razorpay order/payment verification integration
- Forgot-password flow with OTP email support
- Profile image upload support
- PostgreSQL for normal development


## Tech Stack

### Backend

- Java 17
- Spring Boot 3.5.6
- Spring Web
- Spring Security
- JWT authentication
- Spring Data JPA
- PostgreSQL

- JavaMailSender for email
- Razorpay Java SDK
- iText PDF
- Maven

### Frontend

- React 19
- Vite 
- React Router
- Axios
- Tailwind CSS
- Lucide React
- jsPDF

## Project Structure

```text
CBMS-PROJECT/
  backend/
    src/main/java/com/clientbilling/
      config/
      controller/
      dto/
      model/
      repository/
      security/
      service/
    src/main/resources/
      application.properties
      application-local.properties
    pom.xml
  frontend/
    src/
      Auth/
      Components/
      Home/
      Pages/
      context/
      services/
    package.json
  scripts/
  .env.example
```

## Prerequisites

Install these before running the project:

- Java 17
- Node.js 
- npm
- PostgreSQL
- Maven 

## Environment Variables

Copy the sample file and fill in your own values:

```powershell
Copy-Item .env.example .env
```

Important variables:

```env
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_MAIL_USERNAME=your.email@gmail.com
SPRING_MAIL_PASSWORD=your-gmail-app-password
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret
JWT_SECRET=your-secret-key-here


## Database Setup

The default backend configuration expects PostgreSQL on:

```text
jdbc:postgresql://localhost:5432/CBMS-project
```

Create the database in PostgreSQL:

```sql
CREATE DATABASE "CBMS-project";
```

The backend uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created/updated automatically when the app starts.

For quick local testing without PostgreSQL, run the backend with the `local` profile. This uses an in-memory H2 database:

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

## Run Backend

From the `backend` folder:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

API base URL:

```text
http://localhost:8080/api
```

## Run Frontend

From the `frontend` folder:

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

The frontend uses this API URL by default:

```text
http://localhost:8080/api
```

To override it, create `frontend/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Default Admin Login

When the backend starts, it creates a default admin account if one does not already exist:

```text
Username: admin
Email: admin@system.com
Password: admin123
```

Open the app at:

```text
http://localhost:5173
```

Then use the admin login page:

```text
http://localhost:5173/admin/login
```

## Main Frontend Routes

```text
/                         Landing page
/admin/login              Admin login
/client/login             Client login
/admin/dashboard          Admin dashboard
/admin/dashboard/clients  Client management
/admin/dashboard/employees
/admin/dashboard/managers
/admin/dashboard/projects
/admin/dashboard/invoices
/client/dashboard         Client dashboard
/client/dashboard/invoices
/client/dashboard/projects
/client/dashboard/payments
/forgot-password
```

## Useful Backend Endpoints

```text
POST /api/auth/login
POST /api/auth/forgot-password
POST /api/auth/verify-otp
POST /api/auth/reset-password

GET  /api/admin/clients
POST /api/admin/clients
GET  /api/admin/employees
GET  /api/project/view
POST /api/project/add
GET  /api/invoices
GET  /api/payments
POST /api/payments/verify
```

Most admin/client endpoints require a JWT token:

```text
Authorization: Bearer <token>
```

## Email Setup

Email is used for OTP and credential emails. For Gmail, create an app password and set:

```powershell
$env:SPRING_MAIL_USERNAME="your.email@gmail.com"
$env:SPRING_MAIL_PASSWORD="your-gmail-app-password"
```

More email details are available in:

```text
backend/README_EMAIL.md
```

## Razorpay Setup

Set Razorpay credentials before starting the backend:

```powershell
$env:RAZORPAY_KEY_ID="your_razorpay_key_id"
$env:RAZORPAY_KEY_SECRET="your_razorpay_key_secret"
```

The backend reads these values through:

```properties
razorpay.keyId=${RAZORPAY_KEY_ID:your_razorpay_key_id}
razorpay.keySecret=${RAZORPAY_KEY_SECRET:your_razorpay_key_secret}
```

## Build

Build backend:

```powershell
cd backend
.\mvnw.cmd clean package
```

Build frontend:

```powershell
cd frontend
npm run build
```

## Tests

Run backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

Run frontend lint:

```powershell
cd frontend
npm run lint
```

## Notes

- Backend runs on port `8080`.
- Frontend runs on port `5173`.
- Uploaded files are stored under `backend/uploads/`.
- Generated backend build files are stored under `backend/target/`.
- Local log files such as `backend.log` and `frontend.log` are runtime artifacts.
- Keep secrets out of source control.

