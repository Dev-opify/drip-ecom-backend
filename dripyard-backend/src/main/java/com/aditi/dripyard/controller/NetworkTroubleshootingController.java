package com.aditi.dripyard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class NetworkTroubleshootingController {

    @GetMapping("/network-check")
    public ResponseEntity<Map<String, Object>> performNetworkCheck() {
        Map<String, Object> results = new HashMap<>();
        List<String> recommendations = new ArrayList<>();
        
        try {
            // Test DNS resolution for Cloudflare
            boolean cloudflareResolved = testDNSResolution("1.1.1.1", results);
            boolean r2Resolved = testDNSResolution("b5df22bea4df77ebe929092fc8f10dd2.r2.cloudflarestorage.com", results);
            
            // Test internet connectivity
            boolean internetConnected = testInternetConnectivity(results);
            
            // Analyze results and provide recommendations
            if (!cloudflareResolved) {
                recommendations.add("DNS_ISSUE: Cannot resolve Cloudflare DNS. Try changing DNS to 1.1.1.1 and 8.8.8.8");
                recommendations.add("COMMAND: ipconfig /flushdns (Windows) or sudo systemctl restart systemd-resolved (Linux)");
            }
            
            if (!r2Resolved) {
                recommendations.add("R2_DNS_ISSUE: Cannot resolve Cloudflare R2 storage. This indicates network blocking.");
                recommendations.add("SOLUTION: Use VPN or contact network administrator to whitelist *.r2.cloudflarestorage.com");
            }
            
            if (!internetConnected) {
                recommendations.add("CONNECTIVITY_ISSUE: Limited internet access detected");
                recommendations.add("CHECK: Firewall, proxy settings, or network restrictions");
            }
            
            if (cloudflareResolved && r2Resolved && internetConnected) {
                recommendations.add("NETWORK_OK: All connectivity tests passed");
                recommendations.add("ISSUE_LIKELY: Cache or CORS related. Try clearing browser cache.");
            }
            
            results.put("recommendations", recommendations);
            results.put("wifiTroubleshootingSteps", getWifiTroubleshootingSteps());
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            results.put("error", e.getMessage());
            recommendations.add("DIAGNOSTIC_ERROR: Unable to complete network diagnostics");
            results.put("recommendations", recommendations);
            return ResponseEntity.status(500).body(results);
        }
    }
    
    private boolean testDNSResolution(String hostname, Map<String, Object> results) {
        try {
            InetAddress address = InetAddress.getByName(hostname);
            results.put("dns_" + hostname.replace(".", "_"), Map.of(
                "hostname", hostname,
                "resolved", true,
                "ip", address.getHostAddress()
            ));
            return true;
        } catch (Exception e) {
            results.put("dns_" + hostname.replace(".", "_"), Map.of(
                "hostname", hostname,
                "resolved", false,
                "error", e.getMessage()
            ));
            return false;
        }
    }
    
    private boolean testInternetConnectivity(Map<String, Object> results) {
        String[] testHosts = {"google.com", "cloudflare.com", "github.com"};
        int successCount = 0;
        
        for (String host : testHosts) {
            try {
                InetAddress.getByName(host);
                successCount++;
            } catch (Exception ignored) {}
        }
        
        boolean connected = successCount >= 2;
        results.put("internetConnectivity", Map.of(
            "connected", connected,
            "successfulHosts", successCount,
            "totalTested", testHosts.length
        ));
        
        return connected;
    }
    
    private List<Map<String, String>> getWifiTroubleshootingSteps() {
        List<Map<String, String>> steps = new ArrayList<>();
        
        steps.add(Map.of(
            "step", "1",
            "title", "Change DNS Settings",
            "description", "Set DNS to Cloudflare (1.1.1.1, 1.0.0.1) or Google (8.8.8.8, 8.8.4.4)",
            "priority", "HIGH"
        ));
        
        steps.add(Map.of(
            "step", "2", 
            "title", "Clear DNS Cache",
            "description", "Windows: ipconfig /flushdns | Linux: sudo systemctl restart systemd-resolved",
            "priority", "HIGH"
        ));
        
        steps.add(Map.of(
            "step", "3",
            "title", "Test with Different Browser",
            "description", "Try Chrome Incognito, Firefox Private, or different browser entirely",
            "priority", "MEDIUM"
        ));
        
        steps.add(Map.of(
            "step", "4",
            "title", "Check Firewall/Antivirus",
            "description", "Temporarily disable firewall/antivirus to test if they're blocking connections",
            "priority", "MEDIUM"
        ));
        
        steps.add(Map.of(
            "step", "5",
            "title", "Use VPN",
            "description", "If on corporate/school network, use VPN to bypass restrictions",
            "priority", "HIGH"
        ));
        
        steps.add(Map.of(
            "step", "6",
            "title", "Contact Network Admin",
            "description", "Ask to whitelist: *.railway.app, *.r2.cloudflarestorage.com, *.cloudflare.com",
            "priority", "LOW"
        ));
        
        return steps;
    }
    
    @GetMapping("/cors-test")
    public ResponseEntity<Map<String, Object>> corsTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS test successful");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("headers", "All CORS headers should be present");
        
        return ResponseEntity.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "*")
            .body(response);
    }
}