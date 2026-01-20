package com.example.mcp.resources;

import io.modelcontextprotocol.spec.McpSchema.Resource;

import java.util.List;

/**
 * Defines all available resources for the MCP server.
 */
public class ResourceDefinitions {
    
    /**
     * Returns the list of all available resources.
     * 
     * @return list of resource definitions
     */
    public static List<Resource> getResources() {
        return List.of(
            new Resource(
                "resource://search/history",
                "Search History",
                "Recent web search queries and results",
                "application/json",
                null
            ),
            new Resource(
                "resource://config",
                "Server Configuration",
                "Current server configuration settings",
                "application/json",
                null
            )
        );
    }
}
