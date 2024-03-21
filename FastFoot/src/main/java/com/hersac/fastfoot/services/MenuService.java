package com.hersac.fastfoot.services;

import com.hersac.fastfoot.models.Menu;
import com.hersac.fastfoot.repositories.MenuRepositorio;
import java.util.List;

public class MenuService {
    
    private MenuRepositorio menuRepo = new MenuRepositorio(Menu.class);
    
    public List<Menu> getMenus(){
        return menuRepo.findAll();
    }
    
    public Menu  getMenuById(Long id){
        return menuRepo.findById(id);
    }

    public Menu addMenu(Menu nuevoMenu){
        return menuRepo.save(nuevoMenu);
    }
    
    public void updateMenu(Long id, Menu menu){
        menuRepo.update(id, menu);
    }
    
    public void deleteMenu(Long id){
        menuRepo.deleteById(id);
    }
}
