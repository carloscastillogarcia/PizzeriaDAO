import java.util.Optional;

public interface IEntityManager {
    public <T> IEntityManager addStatement(final T entity, String sql, Statement<T> statement);
    public <T> IEntityManager addRangeStatemment(final Iterable<T> iterable,String sql, Statement<T> statement);
    public void save();
    public <T> Optional<T> select(Class<T> clazz, Resultset<T> resultset);
}


