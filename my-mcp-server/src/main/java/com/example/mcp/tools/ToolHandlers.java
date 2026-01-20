package com.example.mcp.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles tool execution for the MCP server.
 */
public class ToolHandlers {
    
    private static final Logger log = LoggerFactory.getLogger(ToolHandlers.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    /**
     * Registers all tool handlers with the MCP server.
     * 
     * @param server the MCP server to register handlers with
     */
    public static void register(McpServer server) {
        // Register tool list handler
        server.addToolListHandler(() -> {
            log.debug("Listing available tools");
            return Mono.just(ToolDefinitions.getTools());
        });
        
        // Register websearch handler
        server.addToolHandler("websearch", ToolHandlers::handleWebSearch);
    }
    
    /**
     * Handles the websearch tool execution.
     * 
     * @param arguments the tool arguments
     * @return the tool response
     */
    private static Mono<CallToolResult> handleWebSearch(JsonNode arguments) {
        log.info("Web search tool called");
        
        if (!arguments.has("query")) {
            return Mono.just(createErrorResult("Missing required 'query' parameter"));
        }
        
        String query = arguments.get("query").asText();
        int numResults = arguments.has("numResults") ? 
            Math.min(arguments.get("numResults").asInt(), 10) : 5;
        
        log.debug("Searching for: {} (max {} results)", query, numResults);
        
        try {
            String searchResults = performWebSearch(query, numResults);
            return Mono.just(createSuccessResult(searchResults));
        } catch (Exception e) {
            log.error("Web search failed", e);
            return Mono.just(createErrorResult("Search failed: " + e.getMessage()));
        }
    }
    
    /**
     * Performs a web search using DuckDuckGo's HTML interface.
     * Note: In production, you would use a proper search API like Google, Bing, or Brave Search.
     * 
     * @param query the search query
     * @param numResults number of results to return
     * @return formatted search results
     */
    private static String performWebSearch(String query, int numResults) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        
        // Using DuckDuckGo Instant Answer API (free, no API key required)
        // Note: For production use, consider using Google Custom Search, Bing Search API, or Brave Search API
        String url = "https://api.duckduckgo.com/?q=" + encodedQuery + "&format=json&no_html=1";
        
        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", "MCP-Server/1.0")
            .get()
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Search request failed with status: " + response.code());
            }
            
            String responseBody = response.body() != null ? response.body().string() : "";
            return formatSearchResults(responseBody, query, numResults);
        }
    }
    
    /**
     * Formats the DuckDuckGo API response into readable search results.
     * 
     * @param jsonResponse the raw JSON response
     * @param query the original search query
     * @param numResults max number of results
     * @return formatted results string
     */
    private static String formatSearchResults(String jsonResponse, String query, int numResults) {
        StringBuilder results = new StringBuilder();
        results.append("# Web Search Results for: ").append(query).append("\n\n");
        
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            int count = 0;
            
            // Abstract (instant answer)
            if (root.has("Abstract") && !root.get("Abstract").asText().isEmpty()) {
                results.append("## Instant Answer\n");
                results.append(root.get("Abstract").asText()).append("\n");
                if (root.has("AbstractURL") && !root.get("AbstractURL").asText().isEmpty()) {
                    results.append("Source: ").append(root.get("AbstractURL").asText()).append("\n");
                }
                results.append("\n");
                count++;
            }
            
            // Related topics
            if (root.has("RelatedTopics") && root.get("RelatedTopics").isArray()) {
                results.append("## Related Results\n\n");
                for (JsonNode topic : root.get("RelatedTopics")) {
                    if (count >= numResults) break;
                    
                    if (topic.has("Text") && !topic.get("Text").asText().isEmpty()) {
                        results.append("### Result ").append(count + 1).append("\n");
                        results.append(topic.get("Text").asText()).append("\n");
                        if (topic.has("FirstURL") && !topic.get("FirstURL").asText().isEmpty()) {
                            results.append("URL: ").append(topic.get("FirstURL").asText()).append("\n");
                        }
                        results.append("\n");
                        count++;
                    }
                }
            }
            
            // If no results found
            if (count == 0) {
                results.append("No direct results found. Try refining your search query.\n");
                results.append("\nTip: For comprehensive web search, consider using a dedicated search API ");
                results.append("like Google Custom Search, Bing Search API, or Brave Search API.\n");
            }
            
        } catch (Exception e) {
            log.warn("Failed to parse search results", e);
            results.append("Search completed but results could not be parsed.\n");
            results.append("Raw response preview: ").append(jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
        }
        
        return results.toString();
    }
    
    /**
     * Creates a successful tool result with text content.
     * 
     * @param text the result text
     * @return the tool result
     */
    private static CallToolResult createSuccessResult(String text) {
        return new CallToolResult(
            List.of(new TextContent(text)),
            false
        );
    }
    
    /**
     * Creates an error tool result.
     * 
     * @param message the error message
     * @return the error result
     */
    private static CallToolResult createErrorResult(String message) {
        return new CallToolResult(
            List.of(new TextContent("Error: " + message)),
            true
        );
    }
}
