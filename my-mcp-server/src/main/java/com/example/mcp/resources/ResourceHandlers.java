package com.example.mcp.resources;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles resource operations for the MCP server.
 */
public class ResourceHandlers {
    
    private static final Logger log = LoggerFactory.getLogger(ResourceHandlers.class);
    private static final Map<String, Boolean> subscriptions = new ConcurrentHashMap<>();
    private static final List<String> searchHistory = new ArrayList<>();
    
    /**
     * Registers all resource handlers with the MCP server.
     * 
     * @param server the MCP server to register handlers with
     */
    public static void register(McpServer server) {
        // Register resource list handler
        server.addResourceListHandler(() -> {
            log.debug("Listing available resources");
            return Mono.just(ResourceDefinitions.getResources());
        });
        
        // Register resource read handler
        server.addResourceReadHandler(ResourceHandlers::handleRead);
        
        // Register resource subscribe handler
        server.addResourceSubscribeHandler(ResourceHandlers::handleSubscribe);
        
        // Register resource unsubscribe handler
        server.addResourceUnsubscribeHandler(ResourceHandlers::handleUnsubscribe);
    }
    
    /**
     * Adds a search query to the history.
     * 
     * @param query the search query to add
     */
    public static void addToSearchHistory(String query) {
        synchronized (searchHistory) {
            searchHistory.add(String.format("[%s] %s", Instant.now(), query));
            // Keep only last 100 entries
            while (searchHistory.size() > 100) {
                searchHistory.remove(0);
            }
        }
    }
    
    /**
     * Handles reading a resource by URI.
     * 
     * @param uri the resource URI
     * @return the resource content
     */
    private static Mono<ReadResourceResult> handleRead(String uri) {
        log.info("Reading resource: {}", uri);
        
        switch (uri) {
            case "resource://search/history":
                return Mono.just(createTextResult(uri, getSearchHistoryJson()));
                
            case "resource://config":
                return Mono.just(createTextResult(uri, getConfigJson()));
                
            default:
                log.warn("Unknown resource requested: {}", uri);
                return Mono.error(new IllegalArgumentException("Unknown resource URI: " + uri));
        }
    }
    
    /**
     * Gets the search history as JSON.
     * 
     * @return JSON string of search history
     */
    private static String getSearchHistoryJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"searchHistory\": [\n");
        
        synchronized (searchHistory) {
            for (int i = 0; i < searchHistory.size(); i++) {
                json.append("    \"").append(escapeJson(searchHistory.get(i))).append("\"");
                if (i < searchHistory.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
        }
        
        json.append("  ],\n");
        json.append("  \"totalSearches\": ").append(searchHistory.size()).append(",\n");
        json.append("  \"timestamp\": \"").append(Instant.now()).append("\"\n");
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Gets the server configuration as JSON.
     * 
     * @return JSON string of configuration
     */
    private static String getConfigJson() {
        return """
            {
              "serverName": "my-mcp-server",
              "version": "1.0.0",
              "tools": ["websearch"],
              "capabilities": {
                "tools": true,
                "resources": true,
                "prompts": true
              },
              "searchConfig": {
                "maxResults": 10,
                "defaultResults": 5,
                "timeout": 30
              },
              "timestamp": "%s"
            }
            """.formatted(Instant.now());
    }
    
    /**
     * Creates a text resource result.
     * 
     * @param uri the resource URI
     * @param content the text content
     * @return the read resource result
     */
    private static ReadResourceResult createTextResult(String uri, String content) {
        return new ReadResourceResult(
            List.of(new TextResourceContents(uri, "application/json", content))
        );
    }
    
    /**
     * Escapes special characters for JSON strings.
     * 
     * @param text the text to escape
     * @return escaped text
     */
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Handles resource subscription.
     * 
     * @param uri the resource URI to subscribe to
     * @return empty mono
     */
    private static Mono<Void> handleSubscribe(String uri) {
        log.info("Client subscribed to resource: {}", uri);
        subscriptions.put(uri, true);
        return Mono.empty();
    }
    
    /**
     * Handles resource unsubscription.
     * 
     * @param uri the resource URI to unsubscribe from
     * @return empty mono
     */
    private static Mono<Void> handleUnsubscribe(String uri) {
        log.info("Client unsubscribed from resource: {}", uri);
        subscriptions.remove(uri);
        return Mono.empty();
    }
}
