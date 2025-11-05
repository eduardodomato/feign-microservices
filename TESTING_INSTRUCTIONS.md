# Testing Instructions for Feign Microservices

## Prerequisites
- Java 21 installed
- Maven installed (or use the provided `mvnw` wrapper)

## Step 1: Build the Project

From the root directory (`feign-microservices`), run:

```bash
mvn clean install
```

Or using the Maven wrapper:
```bash
./mvnw clean install
```

This will build all modules:
- `common-dto`
- `product-microservice`
- `stock-microservice`

## Step 2: Start the Stock Microservice

Open a terminal and navigate to the root directory, then run:

```bash
cd stock-microservice
mvn spring-boot:run
```

Or using the wrapper:
```bash
cd stock-microservice
../mvnw spring-boot:run
```

**Expected output:**
- Stock microservice should start on port **8098**
- Look for: `Started StockMicroserviceApplication in X.XXX seconds`

## Step 3: Start the Product Microservice

Open a **new terminal** (keep stock service running) and navigate to the root directory, then run:

```bash
cd product-microservice
mvn spring-boot:run
```

Or using the wrapper:
```bash
cd product-microservice
../mvnw spring-boot:run
```

**Expected output:**
- Product microservice should start on port **8097**
- Feign client should be initialized
- Look for: `Started ProductMicroserviceApplication in X.XXX seconds`

## Step 4: Test the Endpoints

### Test 1: Create a Product (POST to Product Service)

This will test the full flow: Product Service → Stock Service communication via Feign.

**Using cURL:**
```bash
curl -X POST http://localhost:8097/api/product \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "This is a test product",
    "imageURL": "https://example.com/image.jpg"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Test Product",
  "description": "This is a test product",
  "imageURL": "https://example.com/image.jpg"
}
```

**What to verify:**
- Product is saved in the H2 database
- Stock service receives the notification (check stock service logs)
- Response includes the generated `id` field

### Test 2: Get All Products (GET from Product Service)

```bash
curl http://localhost:8097/api/product
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "Test Product",
    "description": "This is a test product",
    "imageURL": "https://example.com/image.jpg"
  }
]
```

### Test 3: Direct Test of Stock Service

Test the stock service endpoint directly:

```bash
curl -X POST http://localhost:8098/api/stock \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Direct Test",
    "description": "Testing stock service directly",
    "imageURL": "https://example.com/test.jpg"
  }'
```

**Expected Response:**
```json
"Received product: Direct Test"
```

## Step 5: Verify Database (Optional)

### Access H2 Console

1. Open browser and go to: `http://localhost:8097/h2-console`
2. Use these connection settings:
   - **JDBC URL:** `jdbc:h2:mem:testdb`
   - **Username:** `sa`
   - **Password:** (leave empty)
3. Click "Connect"
4. Run query: `SELECT * FROM products;`
5. Verify your created products are stored

## Step 6: Check Logs for Communication

### Check Product Service Logs
Look for Feign client calls to stock service:
```
[INFO] ... Feign client calling stock service ...
```

### Check Stock Service Logs
Look for incoming requests:
```
[INFO] ... POST /api/stock ...
```

## Troubleshooting

### Issue: Port Already in Use
- **Solution:** Stop any processes using ports 8097 or 8098
- Windows: `netstat -ano | findstr :8097` then `taskkill /PID <pid> /F`
- Linux/Mac: `lsof -ti:8097 | xargs kill -9`

### Issue: Feign Client Connection Error
- **Verify:** Stock service is running on port 8098
- **Check:** `application.services.stock.url` in product-microservice properties
- **Note:** There's a leading space in the URL - should be `http://localhost:8098` (no space)

### Issue: Product Not Saving
- **Check:** H2 database is initialized
- **Verify:** JPA is creating tables (check logs for DDL)
- **Access:** H2 console to verify table exists

### Issue: 404 Not Found
- **Verify:** Services are running on correct ports (8097 and 8098)
- **Check:** Endpoint paths match exactly (`/api/product` and `/api/stock`)

### Issue: Compilation Errors
- **Run:** `mvn clean install` from root directory
- **Verify:** All modules build successfully
- **Check:** Java version is 21

## Testing with Postman/Insomnia

### Product Service Endpoints:
- **Base URL:** `http://localhost:8097`
- **GET** `/api/product` - Get all products
- **POST** `/api/product` - Create product

### Stock Service Endpoints:
- **Base URL:** `http://localhost:8098`
- **POST** `/api/stock` - Receive product notification

### Sample Request Body for POST:
```json
{
  "name": "Sample Product",
  "description": "Product description here",
  "imageURL": "https://example.com/image.png"
}
```

## Success Criteria

✅ Both microservices start without errors
✅ Product can be created via POST to product service
✅ Product is saved to H2 database
✅ Stock service receives notification (check logs)
✅ GET endpoint returns all products with IDs
✅ Stock service responds correctly when called directly

