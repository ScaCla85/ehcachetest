package ehcache.testproject.ehcachetest;

import java.io.File;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		CacheManager cacheManager = cacheManagerGenerator();

		Cache<Long, String> preConfigured = cacheManager.getCache("preConfigured", Long.class, String.class);

		Cache<Long, String> myCache = cacheManager.createCache("myCache",
				CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.KB).disk(10, MemoryUnit.MB))
						.build());

		for(int i=0; i<100000; i++) {
			myCache.put(i+1L, "element " + (i+1));
		}
		
		int count = 0;
		
		for(Cache.Entry<Long, String> entry : myCache) {
			count++;
		}
		
		System.out.println("Cache size: " + count);
		
		for(int i=0; i<100000; i++) {
			System.out.println("KeyElement:" + (i+1) + " ValueElement:" + myCache.get(i+1L));
		
			if((i%2)==0) {
				myCache.remove(i+1L);
			}
		}

		count = 0;
		
		for(Cache.Entry<Long, String> entry : myCache) {
			count++;
		}
		
		System.out.println("Cache size: " + count);
		

		cacheManager.close();
	}

	private static CacheManager cacheManagerGenerator() {
		PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				  .with(CacheManagerBuilder.persistence(new File("/tmp", "myData")))
				  .withCache("threeTieredCache",
				    CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
				      ResourcePoolsBuilder.newResourcePoolsBuilder()
				        .heap(10, EntryUnit.ENTRIES)
				        .offheap(1, MemoryUnit.MB)
				        .disk(20, MemoryUnit.MB, true)
				    )
				  ).build(true);
		return cacheManager;
	}
}
