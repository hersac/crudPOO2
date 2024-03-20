package com.hersac.fastfoot.config;

import com.hersac.fastfoot.config.RelacionEntidad;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class Repositorio<T, DATO> {
    private Conexion conn;
    private String nombreTabla;
    private Statement statement;
    private Class<T> claseEntidad;

    public Repositorio(Class<T> claseEntidad) {
        this.conn = new Conexion(); // Inicializa la conexión
        this.nombreTabla = obtenerNombreTabla();

        this.claseEntidad = claseEntidad;

        try {
            this.statement = conn.createStatement();
            String query = generarQueryCreacionTabla();
            this.statement.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String obtenerNombreTabla() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<T> tipoEntidad = (Class<T>) superClass.getActualTypeArguments()[0]; // Corregido aquí
        return tipoEntidad.getSimpleName().toLowerCase();
    }

    private String generarQueryCreacionTabla() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ").append(nombreTabla).append(" (");

        Field[] fields = obtenerCamposClase();
        boolean esPrimerCampo = true;
        for (Field field : fields) {
            String nombreCampo = field.getName();
            Class<?> tipoCampo = field.getType();
            if (field.isAnnotationPresent(RelacionEntidad.class)) {
                RelacionEntidad relacionEntidad = field.getAnnotation(RelacionEntidad.class);
                String nombreTablaRelacionada = relacionEntidad.nombreTabla();
                String nombreCampoClave = relacionEntidad.nombreCampoClave();
                queryBuilder.append(nombreCampo + " BIGINT, FOREIGN KEY (" + nombreCampo + ") REFERENCES "
                        + nombreTablaRelacionada + "(" + nombreCampoClave + "), ");
            } else {
                queryBuilder.append(nombreCampo).append(" ").append(obtenerTipoBaseDatos(tipoCampo, esPrimerCampo))
                        .append(", ");
            }
            esPrimerCampo = false;
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(")");

        return queryBuilder.toString();
    }

    private Field[] obtenerCamposClase() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return ((Class<T>) superClass.getActualTypeArguments()[0]).getDeclaredFields();
    }

    private String obtenerTipoBaseDatos(Class<?> tipoCampo, boolean esPrimeraPropiedad) {
        if (esPrimeraPropiedad) {
            if (tipoCampo == int.class || tipoCampo == Integer.class || tipoCampo == long.class
                    || tipoCampo == Long.class) {
                return "SERIAL PRIMARY KEY";
            } else {
                return "VARCHAR(255) PRIMARY KEY";
            }
        } else if (tipoCampo == double.class || tipoCampo == Double.class) {
            return "DOUBLE PRECISION";
        } else if (tipoCampo == long.class || tipoCampo == Long.class || tipoCampo == int.class
                || tipoCampo == Integer.class) {
            return "BIGINT";
        } else if (tipoCampo == String.class) {
            return "VARCHAR(255)";
        } else if (tipoCampo == Boolean.class || tipoCampo == boolean.class) {
            return "VARCHAR(255)";
        }

        throw new IllegalArgumentException("Tipo de campo no compatible: " + tipoCampo);
    }

    public List<T> findAll() {
        List<T> resultados = new ArrayList<>();

        try {
            String query = "SELECT * FROM " + this.nombreTabla;
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    T entidad = mapearResultSetAEntidad(resultSet);
                    resultados.add(entidad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    public T findById(DATO id) {
        T resultado = null;
        Field[] nombresCampos = obtenerCamposClase();
        String campoId = nombresCampos[0].getName();

        try {
            String query = "SELECT * FROM " + this.nombreTabla + " WHERE " + campoId + " = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setObject(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        resultado = mapearResultSetAEntidad(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public T save(T entidad) {
        T resultado = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("INSERT INTO ").append(this.nombreTabla).append(" (");

            Field[] fields = entidad.getClass().getDeclaredFields();
            boolean primerCampo = true;
            for (Field field : fields) {
                if (primerCampo) {
                    primerCampo = false;
                } else {
                    String nombreCampo = field.getName();
                    queryBuilder.append(nombreCampo).append(", ");
                }
            }

            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
            queryBuilder.append(") VALUES (");
            boolean primerValor = true;
            for (Field field : fields) {
                if (primerValor) {
                    primerValor = false;
                } else if (field.isAnnotationPresent(RelacionEntidad.class)) {
                    field.setAccessible(true);
                    Object valorCampo = field.get(entidad);
                    Field[] camposClaseRelacionada = valorCampo.getClass().getDeclaredFields();
                    if (camposClaseRelacionada.length > 0) {
                        camposClaseRelacionada[0].setAccessible(true);
                        Object valorIdentificador = camposClaseRelacionada[0].get(valorCampo);
                        queryBuilder.append(valorIdentificador).append(", ");
                    }
                } else {
                    field.setAccessible(true);
                    Object valorCampo = field.get(entidad);
                    if (valorCampo instanceof String) {
                        queryBuilder.append("'").append(valorCampo).append("', ");
                    } else {
                        queryBuilder.append(valorCampo).append(", ");
                    }
                }
            }
            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
            queryBuilder.append(")");
            String query = queryBuilder.toString();

            // Ejecutar la solicitud SQL
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.executeUpdate();
            }
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public T update(DATO id, T entidad) {
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE ").append(this.nombreTabla).append(" SET ");

            Field[] fields = entidad.getClass().getDeclaredFields();
            boolean primerCampo = true;
            String campoId = "";
            for (Field field : fields) {
                field.setAccessible(true);
                if (primerCampo) {
                    campoId = field.getName();
                    primerCampo = false;
                } else {
                    String nombreCampo = field.getName();
                    Object valorCampo = field.get(entidad);
                    if (valorCampo != null) {
                        if (valorCampo instanceof String) {
                            queryBuilder.append(nombreCampo).append("='").append(valorCampo).append("', ");
                        } else {
                            queryBuilder.append(nombreCampo).append("=").append(valorCampo).append(", ");
                        }
                    }
                }
            }

            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
            queryBuilder.append(" WHERE ").append(campoId).append(" = ?");
            String query = queryBuilder.toString();

            // Ejecutar la solicitud SQL
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                int index = 1;
                for (Field field : fields) {
                    if (field.getName().equals(campoId)) {
                        statement.setObject(index++, id);
                    }
                }
                statement.executeUpdate();
            }
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return entidad;
    }

    public void deleteById(DATO id) {
        T resultado = null;
        Field[] nombresCampos = obtenerCamposClase();
        String campoId = nombresCampos[0].getName();

        try {
            String query = "DELETE FROM " + this.nombreTabla + " WHERE " + campoId + " = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setObject(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        resultado = mapearResultSetAEntidad(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Se elimino correctamente");
    }

    public T findByField(String nombreCampo, Object valor) {
        T resultado = null;

        try {
            String query = "SELECT * FROM " + this.nombreTabla + " WHERE " + nombreCampo + " = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setObject(1, valor);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        resultado = mapearResultSetAEntidad(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    // Método dinámico para buscar por un campo específico
    public T findBy(String nombreCampo, Object valor) {
        return findByField(nombreCampo, valor);
    }

    public List<T> findAllWithJoins() {
        List<T> resultados = new ArrayList<>();

        try {
            List<String> tablasRelacionadas = obtenerTablasRelacionadas();
            List<Class<?>> clasesRelacionadas = obtenerClasesRelacionadas();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT ");

            // Obtener campos de la clase T utilizando la clase de la entidad
            Field[] campos = claseEntidad.getDeclaredFields();
            for (Field campo : campos) {
                queryBuilder.append(this.nombreTabla.charAt(0) + "." + campo.getName()).append(", ");
            }

            //Obtener campos de las clases relacionadas a T
            if (!clasesRelacionadas.isEmpty()) {
                for (Class<?> claseEntidadRel : clasesRelacionadas) {
                    Field[] camposRel = claseEntidadRel.getDeclaredFields();
                    for(Field campoRel : camposRel){
                        queryBuilder.append(claseEntidadRel.getName().charAt(0) + "." + campoRel.getName()).append(", ");
                    }
                }
            }

            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // Eliminar la coma final
            queryBuilder.append(" FROM ").append(this.nombreTabla + " " + this.nombreTabla.charAt(0));

            // Verificar las relaciones de las entidades
            
            if (!tablasRelacionadas.isEmpty()) {
                for (String tabla : tablasRelacionadas) {
                    queryBuilder.append(" INNER JOIN ").append(tabla + " " + tabla.charAt(0))
                            .append(" ON ").append(this.nombreTabla.charAt(0))
                            .append(".").append(tabla.toLowerCase()).append("id")
                            .append(" = ").append(tabla.charAt(0) + "." + tabla).append("id");
                }
            }

            String query = queryBuilder.toString();

            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    T entidad = mapearResultSetAEntidad(resultSet);
                    resultados.add(entidad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    // Método para obtener las tablas relacionadas utilizando reflexión
    private List<String> obtenerTablasRelacionadas() {
        List<String> tablasRelacionadas = new ArrayList<>();
        Field[] campos = claseEntidad.getDeclaredFields();

        for (Field campo : campos) {
            if (campo.isAnnotationPresent(RelacionEntidad.class)) {
                RelacionEntidad relacionEntidad = campo.getAnnotation(RelacionEntidad.class);
                tablasRelacionadas.add(relacionEntidad.nombreTabla());
            }
        }

        return tablasRelacionadas;
    }

    public List<Class<?>> obtenerClasesRelacionadas() {
    List<Class<?>> clasesRelacionadas = new ArrayList<>();
    Field[] campos = claseEntidad.getDeclaredFields();

    for (Field campo : campos) {
        if (campo.isAnnotationPresent(RelacionEntidad.class)) {
            RelacionEntidad relacionEntidad = campo.getAnnotation(RelacionEntidad.class);
            String nombreClase = relacionEntidad.nombreClase();
            try {
                Class<?> claseRelacionada = Class.forName(nombreClase);
                clasesRelacionadas.add(claseRelacionada);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    return clasesRelacionadas;
}

    protected abstract T mapearResultSetAEntidad(ResultSet resultSet) throws SQLException;
}
