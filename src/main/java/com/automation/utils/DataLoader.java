package com.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.io.IOException;

public class DataLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Carga un archivo JSON desde el classpath y lo convierte a un objeto del tipo especificado.
     * 
     * @param filePath Ruta del archivo JSON en el classpath (ej: "data/user.json")
     * @param clazz Clase destino para la deserialización
     * @param <T> Tipo genérico del objeto a retornar
     * @return Objeto deserializado desde el JSON
     * @throws RuntimeException si ocurre un error al leer o parsear el JSON
     */
    public static <T> T loadJson(String filePath, Class<T> clazz) {
        try {
            InputStream inputStream = DataLoader.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("No se encontró el archivo: " + filePath);
            }
            
            T object = objectMapper.readValue(inputStream, clazz);
            inputStream.close();
            return object;
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo JSON: " + filePath, e);
        }
    }

    /**
     * Carga un archivo JSON como string desde el classpath.
     * 
     * @param filePath Ruta del archivo JSON en el classpath (ej: "data/user.json")
     * @return Contenido del archivo JSON como string
     * @throws RuntimeException si ocurre un error al leer el archivo
     */
    public static String loadJsonAsString(String filePath) {
        try {
            InputStream inputStream = DataLoader.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("No se encontró el archivo: " + filePath);
            }
            
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            String jsonString = objectMapper.writeValueAsString(jsonNode);
            inputStream.close();
            return jsonString;
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo JSON como string: " + filePath, e);
        }
    }

    /**
     * Carga un archivo JSON y lo convierte a un JsonNode para acceso flexible.
     * 
     * @param filePath Ruta del archivo JSON en el classpath (ej: "data/user.json")
     * @return JsonNode para acceso dinámico a los datos
     * @throws RuntimeException si ocurre un error al leer o parsear el JSON
     */
    public static JsonNode loadJsonAsNode(String filePath) {
        try {
            InputStream inputStream = DataLoader.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("No se encontró el archivo: " + filePath);
            }
            
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            inputStream.close();
            return jsonNode;
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo JSON como JsonNode: " + filePath, e);
        }
    }
}
