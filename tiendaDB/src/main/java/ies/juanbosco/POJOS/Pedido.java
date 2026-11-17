package ies.juanbosco.POJOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Pedido {
    private int id;
    private Date fechaPedido;
    private int cantidad;
    private int idproducto;
    private String nombre_producto;

    public Pedido(Date fechaPedido, int cantidad, int idproducto) {
        this.fechaPedido = fechaPedido;
        this.cantidad = cantidad;
        this.idproducto = idproducto;
    }
}
