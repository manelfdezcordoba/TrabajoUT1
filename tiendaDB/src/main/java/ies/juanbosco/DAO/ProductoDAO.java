package ies.juanbosco.DAO;

import ies.juanbosco.POJOS.Producto;

import java.sql.*;
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection", "SqlDialectInspection"})

public class ProductoDAO {

    public boolean insertar(Producto p) throws SQLException{
        String sql = "INSERT INTO productos (nombre, precio , stock) VALUES (?,?,?)";
        try(Connection con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            // la consulta sql es para insertar un nuevo producto , con un id autogenerado
            ps.setString(1, p.getNombre());
            ps.setDouble(2,p.getPrecio());
            ps.setInt(3,p.getStock());

            if (ps.executeUpdate() == 0) return false; // utilizamos el metodo "executeUpdate" para saber si se ha insertado
            try(ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    p.setIdProducto(rs.getInt(1));
                }
            }
            return true;
        }
    }
    public Producto devolverPorId(int idProdu)throws SQLException{
        String sql = "SELECT idproductos, nombre, precio, stock FROM productos WHERE idproductos = ?";
        try(Connection con = ConexionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, idProdu);

            try(ResultSet rs= ps.executeQuery()){
            if (!rs.next()){
            return null;
}//Buscamos el producto sabiendo el id
        return new Producto(rs.getInt("idproductos"),
        rs.getString("nombre"),
        rs.getDouble("precio"),
        rs.getInt("stock"));
            }
        }
    }
    public boolean actualizarStock(int idProducto, int nuevoStock)throws SQLException{
        String sql = "UPDATE productos SET stock = ? WHERE idproductos = ?";
        try (Connection con = ConexionDB.getConexion();
        PreparedStatement ps= con.prepareStatement(sql)){
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }


}
