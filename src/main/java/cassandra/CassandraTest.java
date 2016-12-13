package cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by zhengyong on 16/12/12.
 */
public class CassandraTest {

    public static void main(String[] args) {

        // 创建客户端
        CassandraClient cassandraClient = new CassandraClient("demodb", "127.0.0.1");

        // 插入数据
        cassandraClient.executeSQL("INSERT INTO users (user_name, password, gender) VALUES ('zhangsan', 'admin', 'male')");

        List<String> params = Lists.newArrayList("root", "root", "male");
        cassandraClient.executePreparedSQL("INSERT INTO users (user_name, password, gender) VALUES (?, ?, ?)", params);

        // 查询数据
        ResultSet rs = cassandraClient.executeSQL("select * from users where user_name = 'root'");

        // 输出
        Row row = rs.one();
        System.out.println("row=" + row.toString());
        System.out.println("user_name=" + row.getString("user_name"));
        System.out.println("password=" + row.getString("password"));
        System.out.println("gender=" + row.getString("gender"));

        // 关闭
        cassandraClient.close();
    }
}
