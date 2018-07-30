package views;

import daos.ProdutoDAO;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Produto;

public class App {
    public static void main(String[] args) {
        Produto p = new Produto(2, "Teste", 10f);
        ProdutoDAO dao = new ProdutoDAO();
        
        try {
            dao.save(p);   
            Produto result = dao.get(p.getCodigo());
            System.out.println(result);
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
}
