
package ies.juanbosco;

import ies.juanbosco.DAO.PedidoDAO;
import ies.juanbosco.DAO.ProductoDAO;
import ies.juanbosco.POJOS.Pedido;
import ies.juanbosco.POJOS.Producto;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final ProductoDAO productoDAO = new ProductoDAO();
    private static final PedidoDAO pedidoDAO = new PedidoDAO();
    private static final SimpleDateFormat FORMATO =
            new SimpleDateFormat("yyyy-MM-dd");
/*
Nuestro main nos crea un menu con 6 opciones diferentes para cada necesidad que nos pedia el ejercicio
Este utiliza los metodos creados en las clases DAO para poder ejecutrlos correctamente
utilizando asi la escritura por teclado dentro de la ejecucion del programa gracias al metodo Scanner
 */
    public static void main(String[] args) {

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> insertarProducto();
                    case 2 -> consultarProductoPorId();
                    case 3 -> actualizarStock();
                    case 4 -> insertarPedido();
                    case 5 -> listarPedidos();
                    case 6 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opcion no valida.");
                }

            } catch (SQLException e) {
                System.out.println("ERROR: " + e.getMessage());
            }

        } while (opcion != 6);
    }

    private static void mostrarMenu() {

        System.out.println("\nMENU PRODUCTOS-PEDIDOS");
        System.out.println("1 - Insertar producto");
        System.out.println("2 - Consultar producto por id");
        System.out.println("3 - Actualizar stock de producto");
        System.out.println("4 - Insertar pedido");
        System.out.println("5 - Listado de pedidos");
        System.out.println("6 - Salir");
    }



    private static void insertarProducto() throws SQLException {

        System.out.println("\n--- NUEVO PRODUCTO ---");

        String nombre = leerTexto("Nombre: ");
        double precio = leerDecimal("Precio: ");
        int stock = leerEntero("Stock: ");

        if (nombre.equals("")) {
            System.out.println("El nombre no puede estar vacio.");
            return;
        }

        if (precio < 0 || stock < 0) {
            System.out.println("Precio o stock incorrecto.");
            return;
        }

        Producto p = new Producto(0, nombre, precio, stock);

        if (productoDAO.insertar(p)) {
            System.out.println("Producto insertado con id "
                    + p.getIdProducto());
        } else {
            System.out.println("No se ha podido insertar el producto.");
        }
    }



    private static void consultarProductoPorId() throws SQLException {

        System.out.println("\n--- CONSULTAR PRODUCTO ---");

        int id = leerEntero("Id del producto: ");

        Producto p = productoDAO.devolverPorId(id);

        if (p == null) {
            System.out.println("No existe ningun producto con id " + id);
        } else {
            System.out.println("Id: " + p.getIdProducto()
                    + " | Nombre: " + p.getNombre()
                    + " | Precio: " + p.getPrecio()
                    + " | Stock: " + p.getStock());
        }
    }



    private static void actualizarStock() throws SQLException {

        System.out.println("\n--- ACTUALIZAR STOCK ---");

        int id = leerEntero("Id del producto: ");
        int nuevoStock = leerEntero("Nuevo stock: ");

        if (nuevoStock < 0) {
            System.out.println("El stock no puede ser negativo.");
            return;
        }

        if (productoDAO.actualizarStock(id, nuevoStock)) {
            System.out.println("Stock actualizado correctamente.");
        } else {
            System.out.println("No existe ningun producto con id " + id);
        }
    }


    private static void insertarPedido() throws SQLException {

        System.out.println("\n--- NUEVO PEDIDO ---");

        Date fecha = leerFecha("Fecha (aaaa-mm-dd), vacio = hoy: ");
        int cantidad = leerEntero("Cantidad: ");
        int idProducto = leerEntero("Id del producto: ");

        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor que cero.");
            return;
        }

        Pedido p = new Pedido(fecha, cantidad, idProducto);

        pedidoDAO.insertar(p);

        System.out.println("Pedido insertado con id " + p.getId());
        System.out.println("Stock descontado correctamente.");
    }



    private static void listarPedidos() throws SQLException {

        System.out.println("\n--- LISTADO DE PEDIDOS ---");

        List<Pedido> pedidos = pedidoDAO.listar();

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }

        for (Pedido p : pedidos) {
            System.out.println("Id: " + p.getId()
                    + " | Fecha: " + FORMATO.format(p.getFechaPedido())
                    + " | Cantidad: " + p.getCantidad()
                    + " | Producto: " + p.getNombre_producto());
        }
    }



    private static String leerTexto(String mensaje) {

        System.out.print(mensaje);
        return sc.nextLine().trim();
    }



    private static int leerEntero(String mensaje) {

        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero entero valido.");
            }
        }
    }



    private static double leerDecimal(String mensaje) {

        while (true) {
            try {
                return Double.parseDouble(
                        leerTexto(mensaje).replace(',', '.')
                );
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero decimal valido.");
            }
        }
    }



    private static Date leerFecha(String mensaje) {
//Utilizaremos el formato de fecha yyyy-mm-dd , esta fehca entra siendo un String
        //Para comprobar que no esta vacia haremos un if que comprobamos si hay longitud en la  cadena
        //En caso de que sea true ,es decir que este vacio , devolvera la fecha de hoy , si no esta vacio
        //la fecha introducidad de forma texto , cambiara a formato date
        FORMATO.setLenient(false);

        while (true) {

            String entrada = leerTexto(mensaje);

            if (entrada.isEmpty()) {
                return new Date();
            }

            try {
                return FORMATO.parse(entrada);
            } catch (ParseException e) {
                System.out.println(
                        "Fecha incorrecta. Usa el formato aaaa-mm-dd."
                );
            }
        }
    }
}
//