import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.civitasv.spider.api.AMapKeys;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.mapper.TaskMapper;
import com.civitasv.spider.model.bo.POI;
import com.civitasv.spider.model.po.TaskPo;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import java.util.List;

public class RetrofitTest {
    @Test
    public void testTimeout(){
        AMapDao mapDao = new AMapDaoImpl();

        POI poi = mapDao.getPoi(AMapKeys.getAmapKeys().get(0), "", "KFC", "", "base", 1, 20);
        System.out.println(poi);
    }

    @Test
    public void testMybatis(){
        MyBatisUtils.getDefaultMybatisPlus();
    }

    @Test
    public void testService(){
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            List<TaskPo> taskPos = taskMapper.selectList(new QueryWrapper<>());
            System.out.println(taskPos);
        }

    }
}
