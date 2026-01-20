package com.example.mcp;

import com.example.mcp.tools.ToolDefinitions;
import com.example.mcp.resources.ResourceDefinitions;
import com.example.mcp.prompts.PromptDefinitions;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MCP server components.
 */
class McpServerTest {
    
    @Test
    void testToolDefinitions() {
        List<Tool> tools = ToolDefinitions.getTools();
        
        assertNotNull(tools);
        assertFalse(tools.isEmpty());
        
        // Verify websearch tool exists
        Tool websearchTool = tools.stream()
            .filter(t -> "websearch".equals(t.name()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(websearchTool, "websearch tool should be defined");
        assertNotNull(websearchTool.description());
        assertNotNull(websearchTool.inputSchema());
    }
    
    @Test
    void testResourceDefinitions() {
        List<Resource> resources = ResourceDefinitions.getResources();
        
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
        assertEquals(2, resources.size());
        
        // Verify search history resource
        Resource historyResource = resources.stream()
            .filter(r -> "resource://search/history".equals(r.uri()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(historyResource, "search history resource should be defined");
        assertEquals("Search History", historyResource.name());
        
        // Verify config resource
        Resource configResource = resources.stream()
            .filter(r -> "resource://config".equals(r.uri()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(configResource, "config resource should be defined");
        assertEquals("Server Configuration", configResource.name());
    }
    
    @Test
    void testPromptDefinitions() {
        List<Prompt> prompts = PromptDefinitions.getPrompts();
        
        assertNotNull(prompts);
        assertFalse(prompts.isEmpty());
        assertEquals(2, prompts.size());
        
        // Verify research prompt
        Prompt researchPrompt = prompts.stream()
            .filter(p -> "research".equals(p.name()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(researchPrompt, "research prompt should be defined");
        assertNotNull(researchPrompt.arguments());
        assertFalse(researchPrompt.arguments().isEmpty());
        
        // Verify fact-check prompt
        Prompt factCheckPrompt = prompts.stream()
            .filter(p -> "fact-check".equals(p.name()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(factCheckPrompt, "fact-check prompt should be defined");
        assertNotNull(factCheckPrompt.arguments());
    }
    
    @Test
    void testWebsearchToolInputSchema() {
        List<Tool> tools = ToolDefinitions.getTools();
        Tool websearchTool = tools.stream()
            .filter(t -> "websearch".equals(t.name()))
            .findFirst()
            .orElseThrow();
        
        @SuppressWarnings("unchecked")
        var schema = (java.util.Map<String, Object>) websearchTool.inputSchema();
        
        assertEquals("object", schema.get("type"));
        assertNotNull(schema.get("properties"));
        assertNotNull(schema.get("required"));
        
        @SuppressWarnings("unchecked")
        var required = (java.util.List<String>) schema.get("required");
        assertTrue(required.contains("query"), "query should be required");
    }
    
    @Test
    void testResearchPromptArguments() {
        List<Prompt> prompts = PromptDefinitions.getPrompts();
        Prompt researchPrompt = prompts.stream()
            .filter(p -> "research".equals(p.name()))
            .findFirst()
            .orElseThrow();
        
        assertEquals(2, researchPrompt.arguments().size());
        
        var topicArg = researchPrompt.arguments().stream()
            .filter(a -> "topic".equals(a.name()))
            .findFirst()
            .orElseThrow();
        
        assertTrue(topicArg.required(), "topic argument should be required");
        
        var depthArg = researchPrompt.arguments().stream()
            .filter(a -> "depth".equals(a.name()))
            .findFirst()
            .orElseThrow();
        
        assertFalse(depthArg.required(), "depth argument should be optional");
    }
}
