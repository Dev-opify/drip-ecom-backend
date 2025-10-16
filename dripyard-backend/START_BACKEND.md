# Start Backend Server

## Important: Database Password Required

The backend needs your Railway MySQL database password to start.

## Steps to Start:

1. Open PowerShell in the backend directory
2. Set the database password as an environment variable
3. Run the backend

```powershell
# Navigate to backend directory
cd d:\dripyard-final\drip-ecom-backend\dripyard-backend

# Set your Railway database password (REPLACE WITH YOUR ACTUAL PASSWORD)
$env:SPRING_DATASOURCE_PASSWORD="YOUR_RAILWAY_DB_PASSWORD_HERE"

# Start the backend
.\mvnw.cmd spring-boot:run
```

## How to Find Your Railway Password:

1. Go to https://railway.app
2. Open your MySQL database service
3. Go to "Variables" or "Connect" tab
4. Copy the password value

## Alternative: Create .env file or application-local.properties

You can also create a file at:
`src/main/resources/application-local.properties`

With content:
```properties
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

Then run with:
```powershell
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=local
```

---

## After Backend Starts Successfully:

The coupon issue has been FIXED! Changes made:

### ✅ Fixed Issues:
1. **Removed the "coupon already used" check** - Users can now apply any coupon multiple times
2. **Fixed minimum order value check** - Changed from `<=` to `<` 
3. **Better error messages** - Now shows specific reasons why coupon failed
4. **Improved validation** - Checks if another coupon is already applied
5. **Removed usedCoupons tracking** - Simplified the logic

### 🎯 How It Now Works:
- Apply any coupon that meets criteria (minimum order, active, valid dates)
- Remove coupon easily
- Apply a different coupon after removing the first one
- Can reapply the same coupon multiple times
- Clear error messages when validation fails

### 🧪 Test After Backend Starts:
1. Go to: http://127.0.0.1:5500/Dripyard/cart/index.html
2. Add items to cart (make sure total meets minimum order value)
3. Try different coupons - they should all work now!
4. Remove and reapply coupons - should work smoothly
