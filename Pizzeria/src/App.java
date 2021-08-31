import java.util.UUID;

public class App {
    public static void main(String[] args) throws Exception {
        UUID ingredientID = UUID.randomUUID();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientID);
        ingredient.setName("Masa");
        ingredient.setPrice(2.0);

        EntityManager.buildConnection(Configuration.getConfiguration())
                        .addStatement(ingredient, "INSERT INTO ingredient(id, name, price) VALUES(?, ?, ?)", (statement, entity) ->{
                            statement.setBytes(1, UUIDConverter.fromUUIDtoByteArray(entity.getId()));
                            statement.setString(2, entity.getName());
                            statement.setDouble(3, entity.getPrice());
                        })
                        .save();

        System.out.println("Ingrediente insertado: " + ingredient.getName());
        
        
        Ingredient ingredientSelect = EntityManager.buildConnection(Configuration.getConfiguration())
                                                 .addStatement(ingredient, "SELECT id, name, price FROM ingredient WHERE id=?", (statement, entity) -> {
                                                    statement.setBytes(1, UUIDConverter.fromUUIDtoByteArray(ingredientID));
                                                 })
                                                 .select(Ingredient.class, (resultSet, entity) -> {
                                                    entity.setId(UUIDConverter.fromByteArrayToUUID(resultSet.getBytes("id")));
                                                    entity.setName(resultSet.getString("name"));
                                                    entity.setPrice(resultSet.getDouble("price"));
                                                 }).orElseThrow();

        System.out.println("Ingrediente obtenido: "+ingredientSelect.getName()+", with price: "+ ingredientSelect.getPrice());

        
    }

   
}
