package co.jmurillo.java.jdbc.repositorio;

import co.jmurillo.java.jdbc.modelo.Categoria;
import co.jmurillo.java.jdbc.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Implementación del repositorio para la entidad Producto
public class ProductoRepositorioImpl implements Repositorio<Producto> {

    // Obtiene una conexión a la base de datos
    private Connection conn;

    public ProductoRepositorioImpl(Connection conn) {
        this.conn = conn;
    }

    public ProductoRepositorioImpl() {
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    @Override
    // Recupera todos los productos de la base de datos
    public List<Producto> listar() throws SQLException {
        List<Producto> allProducs = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.*, c.nombre as categoria FROM productos as p " +
                     "inner join categorias as c ON (p.categoria_id = c.id)")) {

            // Itera sobre cada fila del resultado y crea un objeto Producto
            while (rs.next()) {
                Producto p = crearProducto(rs);
                allProducs.add(p);
            }

        }

        return allProducs;
    }

    @Override
    // Busca un producto por su ID
    public Producto porId(Long id) throws SQLException {
        Producto productById = null;

        try (PreparedStatement stmt = conn.prepareStatement("SELECT p.*, c.nombre as categoria FROM productos as p " +
                        "inner join categorias as c ON (p.categoria_id = c.id) WHERE p.id = ?")) {

            // Configura el parámetro para la consulta
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { // Cierra el ResultSet Automaticamete

                // Si encuentra un resultado, crea un objeto Producto
                if (rs.next()) {
                    productById = crearProducto(rs);
                }
            }
        }

        return productById;
    }
    @Override
// Método para guardar un producto en la base de datos
    public Producto guardar(Producto producto) throws SQLException {
        // Validar que la categoría no sea nula
        if (producto.getCategoria() == null) {
            throw new IllegalArgumentException("La categoría del producto no puede ser nula");
        }

        String sql;
        if (producto.getId() != null && producto.getId()>0) {
            sql = "UPDATE productos SET nombre=?, precio=?, categoria_id=?, sku=? WHERE id=?";
        } else {
            sql = "INSERT INTO productos(nombre, precio, categoria_id, sku, fecha_registro) VALUES(?,?,?,?,?)";
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setLong(2, producto.getPrecio());
            stmt.setLong(3, producto.getCategoria().getId());
            stmt.setString(4, producto.getSku());

            if (producto.getId() != null && producto.getId() > 0) {
                stmt.setLong(5, producto.getId());
            } else {
                stmt.setDate(5, new Date(producto.getFechaRegistro().getTime()));
            }

            stmt.executeUpdate();

            if (producto.getId() == null){
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()){
                        producto.setId(rs.getLong(1));
                    }
                }
            }

            return producto;
        }
    }

    @Override
// Método para eliminar un producto de la base de datos
    // Método para eliminar un producto de la base de datos
    public void eliminar(Long id) throws SQLException {
        // Consulta para eliminar un producto según su ID
        String sql = "DELETE FROM productos WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Asigna el ID al parámetro de la consulta
            stmt.setLong(1, id);

            // Ejecuta la consulta SQL
            stmt.executeUpdate();
        }
    }


    // Convierte un ResultSet en un objeto Producto
    private static Producto crearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getLong("id"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getInt("precio"));
        p.setFechaRegistro(rs.getDate("fecha_registro"));
        p.setSku(rs.getString("sku"));
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("categoria_id"));
        categoria.setNombre(rs.getString("categoria"));
        p.setCategoria(categoria);
        return p;
    }
}

