package fr.rci.tools.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.rci.tools.cache.spring.CacheConfigurator;
import fr.rci.tools.cache.test.SimpleTestService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= { CacheConfigurator.class})
public class CacheMethodInterceptorTest {

	@Autowired
	private SimpleTestService testService;

    @Test
    public void testNoCache() {
        for (int i = 0; i < 100; i++) {
            int callCount = testService.getCallCountNoCache("nocache", i);
            assertEquals(1, callCount);
            callCount = testService.getCallCountNoCache("nocache", i);
            assertEquals(2, callCount);
        }
    }

    @Test
    public void testCache() {
        for (int i = 0; i < 100; i++) {
            int callCount = testService.getCallCount("cache", i);
            assertEquals(1, callCount);
            callCount = testService.getCallCount("cache", i);
            assertEquals(1, callCount);
        }
    }

    @Test
    public void testPermCache() {
        for (int i = 0; i < 15; i++) {
            int callCount = testService.getCallCount("permcache", i);
            assertEquals(1, callCount);
        }
        int callCount = testService.getCallCount("permcache", 1);
        assertEquals(1, callCount);
    }
    
    @Test
    public void testDiskCache() {
        byte[] bs = new byte[10*1024*1024];
        for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte) (i % 255) ;
		}
        byte[] bs2 = testService.getLargeValue("diskcache", bs);
        assertEquals(bs, bs2);

        byte[] bs3 = testService.getLargeValue("diskcache", bs);
        assertNotSame(bs2, bs3);
    }

    
    @Test
    public void testCacheNullValue() {
        for (int i = 0; i < 100; i++) {
            testService.getCallCount("nullValueCache");
        }
        int callCount = testService.getCallCount("nullValueCache", 1);
        assertEquals(2, callCount);

    }

}
