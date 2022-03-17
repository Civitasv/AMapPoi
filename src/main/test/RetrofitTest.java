import com.civitasv.spider.api.AMapKeys;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.model.POI;
import org.junit.Test;

public class RetrofitTest {
    @Test
    public void testTimeout(){
        AMapDao mapDao = new AMapDaoImpl();

        POI poi = mapDao.getPoi(AMapKeys.getAmapKeys().get(0), "", "KFC", "", "base", 1, 20);
        System.out.println(poi);
    }
}
