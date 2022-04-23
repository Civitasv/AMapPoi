import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.civitasv.spider.mapper.TaskMapper;
import com.civitasv.spider.model.po.TaskPo;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import java.util.List;

public class RetrofitTest {

    @Test
    public void testMybatis() {
        MyBatisUtils.getDefaultMybatisPlus();
    }

    @Test
    public void testService() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            List<TaskPo> taskPos = taskMapper.selectList(new QueryWrapper<>());
            System.out.println(taskPos);
        }
    }

    @Test
    public void testInteger() {
        System.out.println(new Integer(0) == 0);
    }
}
