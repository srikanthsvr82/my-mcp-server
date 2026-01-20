package com.example.mcp.tools;

import io.modelcontextprotocol.spec.McpSchema.Tool;

import java.util.List;
import java.util.Map;

/**
 * Defines all available tools for the MCP server.
 */
public class ToolDefinitions {
    
    /**
     * Returns the list of all available tools.
     * 
     * @return list of tool definitions
     */
    public static List<Tool> getTools() {
        return List.of(
            createWebSearchTool()
        );
    }
    
    /**
     * Creates the web search tool definition.
     * 
     * @return web search tool
     */
    private static Tool createWebSearchTool() {
        return new Tool(
            "websearch",
            "Search the web for information on any topic. Returns relevant search results with titles, URLs, and snippets.",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "query", Map.of(
                        "type", "string",
                        "description", "The search query to look up on the web"
                    ),
                    "numResults", Map.of(
                        "type", "integer",
                        "description", "Number of search results to return (default: 5, max: 10)"
                    )
                ),
                "required", List.of("query")
            )
        );
    }
}
