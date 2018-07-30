package daos;

import com.google.gson.Gson;
import factory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Produto;
import redis.clients.jedis.Jedis;

public class ProdutoDAO {
    
    public void save(Produto p) throws SQLException{
        saveProdutoBD(p);
    }
    
    public Produto get(int codigo) throws SQLException{
        Produto p = null;
        
        p = getProdutoCache(codigo);
        if(p != null){
            return p;
        }
        
        p = getProdutoBD(codigo);
        if(p != null){
            saveProdutoCache(p);
            return p;
        }
        
        return null;
    }

    private void saveProdutoCache(Produto p) {
        Gson gson = new Gson();
        String pJson = gson.toJson(p);
        String id = Integer.toString(p.getCodigo());

        Jedis jedis = new Jedis("localhost");
        //1800 segundos = 30 minutos
        jedis.setex(id, 1800, pJson);
    }

    private Produto getProdutoCache(int codigo) {
        Jedis jedis = new Jedis("localhost");
        Gson gson = new Gson();

        String id = Integer.toString(codigo);
        String result = jedis.get(id);

        if (result != null) {
            Produto produto = gson.fromJson(result, Produto.class);
            return produto;
        }

        return null;
    }

    private void saveProdutoBD(Produto p) throws SQLException {

        String sql = "INSERT INTO Produto(codigo, descricao, preco)"
                + " VALUES(?, ?, ?)";

        Connection connection = ConnectionFactory.connect();
        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setInt(1, p.getCodigo());
        stmt.setString(2, p.getDescricao());
        stmt.setFloat(3, p.getPreco());
        stmt.executeUpdate();

        stmt.close();
        connection.close();
    }

    private Produto getProdutoBD(int codigo) throws SQLException {
        String sql = "SELECT codigo, descricao, preco FROM Produto"
                + " WHERE codigo = ?";

        Connection connection = ConnectionFactory.connect();
        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setInt(1, codigo);
        ResultSet result = stmt.executeQuery();

        Produto produto = null;
        if (result.next()) {

            produto = new Produto();
            produto.setCodigo(result.getInt("codigo"));
            produto.setDescricao(result.getString("descricao"));
            produto.setPreco(result.getFloat("preco"));
        }

        stmt.close();
        connection.close();

        return produto;
    }

}
