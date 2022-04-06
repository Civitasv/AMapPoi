import com.civitasv.spider.api.AMapKeys;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.mapper.TaskMapper;
import com.civitasv.spider.model.POI;
import com.civitasv.spider.service.serviceImpl.JobServiceImpl;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

public class RetrofitTest {
    @Test
    public void testTimeout(){
        AMapDao mapDao = new AMapDaoImpl();

        POI poi = mapDao.getPoi(AMapKeys.getAmapKeys().get(0), "", "KFC", "", "base", 1, 20);
        System.out.println(poi);
    }

    @Test
    public void testMybatis(){
        MyBatisUtils.getDefaultMyBatis();
    }

    @Test
    public void testService(){
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMyBatis();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
        }

        JobServiceImpl jobService = new JobServiceImpl();
    }
}
