import java.sql.SQLException;
import java.sql.ResultSet;

public interface Resultset<T> {
    public void run(ResultSet resultSet, T entity) throws SQLException;
}
