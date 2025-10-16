# Network Troubleshooting Guide - DripYard Backend

## Quick Fix for WiFi Image Loading Issues

### 🚀 **Most Common Solution (90% success rate)**
1. **Change DNS Settings** to Cloudflare:
   - Primary DNS: `1.1.1.1` 
   - Secondary DNS: `1.0.0.1`
2. **Flush DNS Cache**: Run `ipconfig /flushdns` (Windows)
3. **Restart Browser**

## Enhanced Debugging Features

### 1. **Real-time Logs**
```bash
# Start backend with detailed logging
./start-with-logging.bat

# Or manually monitor logs
Get-Content logs/dripyard-backend.log -Wait
```

### 2. **Network Diagnostic Endpoints**
Visit these URLs to diagnose network issues:

- **Full Network Check**: `http://localhost:8080/api/debug/network-check`
- **Storage Connectivity**: `http://localhost:8080/api/images/debug/network` 
- **CORS Test**: `http://localhost:8080/api/debug/cors-test`

### 3. **Image Loading Error Types**
The backend now provides specific error headers:

| Error Type | Meaning | Solution |
|------------|---------|----------|
| `DNS_RESOLUTION_FAILED` | Cannot resolve Cloudflare R2 hostname | Change DNS to 1.1.1.1 |
| `CONNECTION_FAILED` | Network/Firewall blocking | Use VPN or contact IT |
| `SSL_ERROR` | Certificate/TLS issues | Check system time, trust certificates |
| `IMAGE_NOT_FOUND` | Image missing from storage | Check if image was uploaded |

## WiFi Network-Specific Issues

### Corporate/School Networks
- **Problem**: Network blocks external cloud services
- **Solution**: 
  - Use VPN
  - Ask IT to whitelist: `*.railway.app`, `*.r2.cloudflarestorage.com`, `*.cloudflare.com`

### Home WiFi Issues
- **Problem**: ISP DNS issues
- **Solution**: Change router DNS or device DNS to Cloudflare (1.1.1.1)

### Mobile Hotspot Test
- Connect to mobile hotspot to verify it's network-specific
- If works on mobile but not WiFi → Network restriction confirmed

## Troubleshooting Steps

### Step 1: Quick DNS Fix
```cmd
# Windows
ipconfig /flushdns
# Change DNS to 1.1.1.1 and 1.0.0.1
```

### Step 2: Test Network Connectivity
```bash
# Visit diagnostic endpoint
curl http://localhost:8080/api/debug/network-check
```

### Step 3: Check Browser Console
Look for these error patterns:
- `ERR_NAME_NOT_RESOLVED` → DNS issue
- `ERR_CONNECTION_REFUSED` → Firewall blocking
- `ERR_SSL_PROTOCOL_ERROR` → SSL/TLS issue

### Step 4: Test Different Browsers
- Chrome Incognito mode
- Firefox Private mode  
- Edge

## Configuration Improvements Made

### 1. **Enhanced CORS Configuration**
- Added support for all development ports
- Explicit CORS headers on image responses
- Cross-origin resource policy enabled

### 2. **Better Network Handling**
- Increased connection timeouts for slower networks
- Improved error detection and reporting
- Retry logic for transient failures

### 3. **Comprehensive Logging**
- Debug logs for AWS SDK
- Network request/response logging
- Detailed error stack traces

## Production Deployment

### Railway Environment Variables
Ensure these are set in Railway:
```
CLOUDFLARE_R2_ACCESSKEY=your-key
CLOUDFLARE_R2_SECRETKEY=your-secret  
CLOUDFLARE_R2_ENDPOINT=https://b5df22bea4df77ebe929092fc8f10dd2.r2.cloudflarestorage.com
CLOUDFLARE_R2_BUCKET=bucket1
```

### Monitoring
- Check Railway logs: `railway logs`
- Monitor endpoint: `https://your-app.railway.app/api/debug/network-check`

## Emergency Contact
If none of the above solutions work:
1. **Document the exact error** from browser console
2. **Test the diagnostic endpoints** and save results
3. **Note which WiFi works vs doesn't work**
4. **Contact support** with this information

## Success Indicators
✅ Images load consistently across all WiFi networks  
✅ No CORS errors in browser console  
✅ Diagnostic endpoints return "connected": true  
✅ DNS resolution succeeds for all tested hosts  