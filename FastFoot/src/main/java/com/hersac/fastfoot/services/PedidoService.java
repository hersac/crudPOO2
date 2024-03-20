package com.hersac.fastfoot.services;

import com.hersac.fastfoot.models.Pedido;
import com.hersac.fastfoot.repositories.PedidoRepositorio;
import java.util.List;

public class PedidoService {
    private PedidoRepositorio pedidoRepo = new PedidoRepositorio(Pedido.class);
    
    public List<Pedido> getPedidos(){
        return pedidoRepo.findAllWithJoins();
    }
    
    public Pedido getPedidoById(Long id){
        return pedidoRepo.findById(id);
    }
    
    public Pedido addPedido(Pedido pedido){
        return pedidoRepo.save(pedido);
    }
    
    public Pedido updatePedido(Long id, Pedido pedido) {
        return pedidoRepo.update(id, pedido);
    }
    
    public void deletePedido(Long id){
        pedidoRepo.deleteById(id);
    }
}
