package ies.juanbosco.DAO;
import ies.juanbosco.POJOS.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection", "SqlDialectInspection"})


public class PedidoDAO {
    public void insertar(Pedido p)throws SQLException{
        //Cramos las consultas SQL que utilizaremos para poder primero buscar el stock mediante el id de un producto
        //Luego insertamos el pedido
        // Y para acabar cambiamos la cantidad de stock que tenemos en nuestra tabla producto


        String sqlStock     = "SELECT stock FROM productos WHERE idproductos = ?";
        String sqlInsertar   = "INSERT INTO pedidos (fecha_pedido, cantidad, idproducto) VALUES (?,?,?)";
        String sqlDescuento = "UPDATE productos SET stock = stock - ? WHERE idproductos = ?";


       Connection con = null;
        try{
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);
            int stockActual;
            try(PreparedStatement ps = con.prepareStatement(sqlStock)){
                //Le agregamos al  PreparedStatement la consulta sql

                ps.setInt(1,p.getIdproducto());
                //ps.setInt hace que coge el primer "?" de la consulta al ser un valor desconocido , le decimos que
                //sabiendo el id del producto , nos de el stock de ese id


                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()){
                        throw new SQLException("No existe ningun producto con id" + p.getIdproducto());
                    }
                    stockActual = rs.getInt("stock");
                }
                //Con el resultSet ejecutamos la consulta , buscando con "!rs.next" desde la primera fila del id , si no lo encuentra
                // enetra en el if y manda la excepcion
            }

            if (stockActual < p.getCantidad()){ //En caso de que el stock actual sea menos a la cantidad que pedimos saltara excepcion
                throw new SQLException("Stock insuficiente \n Stock disponible : "+ stockActual);
            }
            try(PreparedStatement ps = con.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)){
                ps.setDate(1, new java.sql.Date(p.getFechaPedido().getTime()));
                ps.setInt(2, p.getCantidad());
                ps.setInt(3, p.getIdproducto());
                ps.executeUpdate();
                try(ResultSet generadas = ps.getGeneratedKeys()){
                    if (generadas.next()){
                        p.setId(generadas.getInt(1));
                    }
                }
            }
            try(PreparedStatement ps = con.prepareStatement(sqlDescuento)){
                ps.setInt(1, p.getCantidad());
                ps.setInt(2, p.getIdproducto());
                ps.executeUpdate();
            }
            con.commit();
        }catch ( SQLException e){
            if(con != null){
                con.rollback();
            }
            throw e;

        }finally{
            if(con != null){
                con.setAutoCommit(true);
                con.close();
            }
        }
    }


public List<Pedido> listar() throws SQLException {
    String sql = "SELECT ped.idpedidos, ped.fecha_pedido, ped.cantidad, ped.idproducto, "
            + "pro.nombre AS nombre_producto "
            + "FROM pedidos ped "
            + "INNER JOIN productos pro ON ped.idproducto = pro.idproductos "
            + "ORDER BY ped.idpedidos";
    //Aqui creamos nuestra consulta SQL que le decimos danos el id , la fecha , la cantidad y el id de producto
    //de la tabla pedidos . A su vez tambien le pedimos que de la tabla productos nos de el id y nombre este lo almacene en
    //la variable creada "nonbre_pedido" y compara que el id producto de la tabla pedido sea el mismo id producto de la tabla producto
        List<Pedido> pedidos = new ArrayList<>();
        try(Connection con = ConexionDB.getConexion();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs= ps.executeQuery()){
            while (rs.next()){
                Pedido p = new Pedido();
                p.setId(rs.getInt("idpedidos"));
                p.setFechaPedido(rs.getDate("fecha_pedido"));
                p.setCantidad(rs.getInt("cantidad"));
                p.setIdproducto(rs.getInt("idproducto"));
                p.setNombre_producto(rs.getString("nombre_producto"));
                pedidos.add(p);
            }
            //aqui almacenamos en una lista de pedidos todos los pedidos
            //ya que la consulta nos da los parametros que necesitamos entonces creamos un nuevo pedido para almacenarlo
        }
        return pedidos;
    }
}
