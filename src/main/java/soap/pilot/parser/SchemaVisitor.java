package soap.pilot.parser;

import io.swagger.v3.oas.models.media.Schema;

public interface SchemaVisitor {
    void visit(String id, Schema schema);

}
