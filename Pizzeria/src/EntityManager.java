import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityManager implements IEntityManager{
    private List<IRunnables> runnables = new ArrayList<IRunnables>();
    private IConfiguration configuration = null;

    public EntityManager(IConfiguration configuration){
        this.configuration = configuration;
    }

    public static EntityManager buildConnection(IConfiguration configuration){
        return new EntityManager(configuration);
    }

    @Override
    public <T> EntityManager addStatement(T entity, String sql, Statement<T> statement) {
        IRunnables runnable = new Runnables<T>(sql, entity, statement);
        this.runnables.add(runnable);
        return this;
    }

    
    @Override
    public <T> IEntityManager addRangeStatemment(Iterable<T> iterable, String sql, Statement<T> statement) {
        for(T entity : iterable){
            IRunnables runable = new Runnables<T>(sql, entity, statement);
            this.runnables.add(runable);
        }
        return this;
    }
    @Override
    public void save() {

        Connection connection = null;

        try{
            connection = DriverManager.getConnection(
                this.configuration.getUrl(),
                this.configuration.getUser(),
                this.configuration.getPassword()
            );
            connection.setAutoCommit(false);

            for(IRunnables runable : this.runnables){
                
                PreparedStatement statement = connection.prepareStatement(runable.getSQL());
                runable.run(statement);
                statement.executeUpdate();
            }
            connection.commit();

        }catch(SQLException e){
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally{

            this.runnables.clear();
            try {
                if(!connection.isClosed()){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> Optional<T> select(Class<T> clazz, Resultset<T> resultset) {
        Connection connection = null;
        T entity = null;

        try{
            connection = DriverManager.getConnection(
                this.configuration.getUrl(),
                this.configuration.getUser(),
                this.configuration.getPassword()
            );
 
            IRunnables runnable = this.runnables.get(0);

            PreparedStatement statement = connection.prepareStatement(runnable.getSQL());
            runnable.run(statement);

            ResultSet resultSetSQL = statement.executeQuery();

            while(resultSetSQL.next()){

                entity = clazz.getConstructor().newInstance();
                resultset.run(resultSetSQL, entity);
            }

        }catch(SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e){
            e.printStackTrace();
        }finally{

            this.runnables.clear();
            try {
                if(!connection.isClosed()){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return Optional.of(entity);
    }

}
