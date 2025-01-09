package soap.pilot.parser;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.Optional;

public class OpenApiParser {
    private OpenAPI openAPI;
    private OpenAPIParseHandler handler;

    private OpenApiParser(OpenAPI openAPI, OpenAPIParseHandler handler) {
        this.handler = handler;
        this.openAPI = openAPI;
    }

    public static void parse(OpenAPI api, OpenAPIParseHandler handler) {
        OpenApiParser parser = new OpenApiParser(api, handler);
        parser.parse();
    }

    private void parse() {
        handler.onStart();
        Optional.ofNullable(openAPI.getServers()).orElseGet(ArrayList::new).stream().forEach(server -> {
            handler.onServer(server);
        });
        Optional.ofNullable(openAPI.getComponents()).map(Components::getSchemas).ifPresent(schemaMap -> {
            if (schemaMap.size() > 0) {
                handler.onStartSchema();
                schemaMap.forEach(handler::onSchema);
                handler.onFinishSchema();
            }
        });
        Optional.ofNullable(openAPI.getPaths()).ifPresent(paths -> {
            handler.onStartPathItem();
            paths.forEach(handler::onPathItem);
            handler.onFinishPathItem();
        });
    }

    public static interface OpenAPIParseHandler {
        void onStart();

        void onServer(Server server);

        void onStartSchema();

        void onSchema(String id, Schema schema);

        void onFinishSchema();

        void onStartPathItem();

        void onPathItem(String path, PathItem pathItem);

        void onFinishPathItem();
    }
}
