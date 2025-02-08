package co.jmurillo.java.jdbc;

import co.jmurillo.java.jdbc.modelo.Categoria;
import co.jmurillo.java.jdbc.modelo.Producto;
import co.jmurillo.java.jdbc.service.CatalogoServiceImpl;
import co.jmurillo.java.jdbc.service.Service;

import java.sql.*;
import java.util.Date;

public class EjemploJdbc {
    public static void main(String[] args) throws SQLException {

        Service service = new CatalogoServiceImpl();

        System.out.println("================ Listar ==============");
        service.listar().forEach(System.out::println);

        Categoria categoria = new Categoria();
        categoria.setNombre("Iluminación");



        System.out.println("============ Insertar Nuevo Producto ===============");
        Producto producto = new Producto();
        producto.setNombre("Lampara led Escritorio");
        producto.setPrecio(99);
        producto.setFechaRegistro(new Date());
        producto.setSku("0000000004");

        service.guardarProductoConCategoria(producto, categoria);
        service.listar().forEach(System.out::println);
        System.out.println("Guardado correctamente!!!!!" + producto.getId());



    }
}
