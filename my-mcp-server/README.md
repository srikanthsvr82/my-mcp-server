# My MCP Server

A Model Context Protocol (MCP) server built with Java and the official MCP Java SDK, providing web search capabilities.

## Features

- ✅ **Tools**: `websearch` - Search the web for information
- ✅ **Resources**: Search history, server configuration
- ✅ **Prompts**: Research assistant, fact-checking
- ✅ Reactive Streams with Project Reactor
- ✅ Structured logging with SLF4J/Logback
- ✅ Unit tests

## Requirements

- Java 17 or later
- Gradle 7+

## Project Structure

```
my-mcp-server/
├── build.gradle.kts
├── src/
│   ├── main/java/com/example/mcp/
│   │   ├── McpServerApplication.java
│   │   ├── tools/
│   │   │   ├── ToolDefinitions.java
│   │   │   └── ToolHandlers.java
│   │   ├── resources/
│   │   │   ├── ResourceDefinitions.java
│   │   │   └── ResourceHandlers.java
│   │   └── prompts/
│   │       ├── PromptDefinitions.java
│   │       └── PromptHandlers.java
│   └── test/java/com/example/mcp/
│       └── McpServerTest.java
└── README.md
```

## Build

```bash
./gradlew build
```

## Run

```bash
./gradlew run
```

Or run the JAR directly:

```bash
java -jar build/libs/my-mcp-server-1.0.0.jar
```

## Testing

```bash
./gradlew test
```

## Available Tools

### websearch

Search the web for information on any topic.

**Parameters:**
- `query` (required): The search query
- `numResults` (optional): Number of results (default: 5, max: 10)

**Example:**
```json
{
  "name": "websearch",
  "arguments": {
    "query": "latest AI developments 2026",
    "numResults": 5
  }
}
```

## Available Resources

| URI | Description |
|-----|-------------|
| `resource://search/history` | Recent web search queries and results |
| `resource://config` | Current server configuration settings |

## Available Prompts

### research

Generate a research prompt for investigating a topic.

**Arguments:**
- `topic` (required): The topic to research
- `depth` (optional): 'quick', 'standard', or 'comprehensive'

### fact-check

Generate a fact-checking prompt to verify claims.

**Arguments:**
- `claim` (required): The claim to fact-check

## Integration with Claude Desktop

Add to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "my-mcp-server": {
      "command": "java",
      "args": ["-jar", "/path/to/my-mcp-server-1.0.0.jar"]
    }
  }
}
```

## Configuration

The server uses DuckDuckGo's Instant Answer API for web search by default. For production use, consider integrating:

- Google Custom Search API
- Bing Search API
- Brave Search API

Update the `ToolHandlers.java` file to use your preferred search provider.

## License

MIT
