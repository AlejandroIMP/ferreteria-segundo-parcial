package com.ferreteriagd.sistemaFerreteria.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase de configuración para la base de datos
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private Properties properties;

    // Configuración por defecto
    private static final String DEFAULT_SERVER = "localhost";
    private static final String DEFAULT_PORT = "1433";
    private static final String DEFAULT_DATABASE = "ferreteria_db";
    private static final String DEFAULT_USERNAME = "";
    private static final String DEFAULT_PASSWORD = "";
    private static final boolean DEFAULT_USE_INTEGRATED_SECURITY = true;
    private static final boolean DEFAULT_ENCRYPT = false;
    private static final boolean DEFAULT_TRUST_SERVER_CERTIFICATE = true;

    public DatabaseConfig() {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.out.println("Archivo de configuración no encontrado. Usando valores por defecto.");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar configuración de base de datos: " + e.getMessage());
        }
    }

    public String getServer() {
        return properties.getProperty("db.server", DEFAULT_SERVER);
    }

    public String getPort() {
        return properties.getProperty("db.port", DEFAULT_PORT);
    }

    public String getDatabase() {
        return properties.getProperty("db.database", DEFAULT_DATABASE);
    }

    public String getUsername() {
        return properties.getProperty("db.username", DEFAULT_USERNAME);
    }

    public String getPassword() {
        return properties.getProperty("db.password", DEFAULT_PASSWORD);
    }

    public boolean useIntegratedSecurity() {
        return Boolean.parseBoolean(properties.getProperty("db.integratedSecurity",
                                                           String.valueOf(DEFAULT_USE_INTEGRATED_SECURITY)));
    }

    public boolean isEncrypt() {
        return Boolean.parseBoolean(properties.getProperty("db.encrypt",
                                                           String.valueOf(DEFAULT_ENCRYPT)));
    }

    public boolean isTrustServerCertificate() {
        return Boolean.parseBoolean(properties.getProperty("db.trustServerCertificate",
                                                           String.valueOf(DEFAULT_TRUST_SERVER_CERTIFICATE)));
    }

    public String getConnectionString() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:sqlserver://")
          .append(getServer())
          .append(":").append(getPort())
          .append(";databaseName=").append(getDatabase())
          .append(";encrypt=").append(isEncrypt())
          .append(";trustServerCertificate=").append(isTrustServerCertificate());

        if (useIntegratedSecurity()) {
            sb.append(";integratedSecurity=true");
        }

        return sb.toString();
    }

    // Método para imprimir la configuración actual (sin mostrar la contraseña)
    public void printConfiguration() {
        System.out.println("=== Configuración de Base de Datos ===");
        System.out.println("Servidor: " + getServer());
        System.out.println("Puerto: " + getPort());
        System.out.println("Base de datos: " + getDatabase());
        System.out.println("Usuario: " + getUsername());
        System.out.println("Contraseña: " + (getPassword().isEmpty() ? "[Vacía]" : "[Configurada]"));
        System.out.println("Seguridad integrada: " + useIntegratedSecurity());
        System.out.println("Encriptación: " + isEncrypt());
        System.out.println("Confiar en certificado: " + isTrustServerCertificate());
        System.out.println("Cadena de conexión: " + getConnectionString());
    }
}
