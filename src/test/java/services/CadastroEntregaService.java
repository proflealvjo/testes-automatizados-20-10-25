package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.EntregaModel;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class CadastroEntregaService {
    final EntregaModel entregaModel = new EntregaModel();
    public final Gson gson = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation()
            .create();
    public Response response;
    String baseUrl = "https://api-entregas-aa641d0d8c56.herokuapp.com";
    String idDelivery;
    JSONObject jsonSchema;
    private final ObjectMapper mapper = new ObjectMapper();
    String schemasPath = "src/test/resources/schemas/";

    public void setFieldsDelivery(String field, String value) {
        switch (field) {
            case "numeroPedido" -> entregaModel.setNumeroPedido(Integer.parseInt(value));
            case "nomeEntregador" -> entregaModel.setNomeEntregador(value);
            case "statusEntrega" -> entregaModel.setStatusEntrega(value);
            case "dataEntrega" -> entregaModel.setDataEntrega(value);
            default -> throw new IllegalStateException("Unexpected field" + field);
        }
    }

    public void createDelivery(String endPoint) {
        String url = baseUrl + endPoint;
        String bodyToSend = gson.toJson(entregaModel);
        response = given().
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(bodyToSend).
                when().
                post(url).
                then().
                extract().
                response();
    }

    public void retrieveIdDelivery() {
        idDelivery = String.valueOf(gson.fromJson(response.jsonPath().prettify(), EntregaModel.class).getNumeroEntrega());
    }

    public void deleteDelivery(String endPoint) {
        String url = String.format("%s%s/%s", baseUrl, endPoint, idDelivery);
        response = given().
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                delete(url).
                then().
                extract().
                response();
    }

    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            JSONTokener tokener = new JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }

    public void setContract(String contract) throws IOException {
        switch (contract) {
            case "Cadastro bem-sucedido de entrega" -> jsonSchema = loadJsonFromFile(schemasPath + "cadastro-bem-sucedido-de-entrega.json");
            default -> throw new IllegalStateException("Unexpected contract" + contract);
        }
    }

    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {

        // Obter o corpo da resposta como String e converter para JSONObject
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        // Configurar o JsonSchemaFactory e criar o JsonSchema
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());
        // Converter o JSON de resposta para JsonNode
        JsonNode jsonResponseNode = mapper.readTree(jsonResponse.toString());
        // Validar o JSON de resposta contra o esquema
        Set<ValidationMessage> schemaValidationErrors = schema.validate(jsonResponseNode);

        return schemaValidationErrors;

    }
}
