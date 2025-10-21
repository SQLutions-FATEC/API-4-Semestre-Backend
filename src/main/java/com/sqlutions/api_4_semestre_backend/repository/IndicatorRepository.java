import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class IndicatorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> searchIndicators() {
        String sql = "SELECT indice_trafego, indice_seguranca FROM indice_rua";
        return jdbcTemplate.queryForList(sql);
    }
}
