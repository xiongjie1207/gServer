package ${packageName};

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import com.gserver.plugins.db.descriptor.QueryResult;
import com.gserver.plugins.db.spring.jdbc.SpringJDBCBuilder;
import org.springframework.aop.framework.AopContext;
${importClasses}

/**
 * Generated by GServer, do not modify this file.
 **/
public abstract class BaseDao${entity} implements IDao${entity} {
	@Cacheable(value = "${cacheName}", key = "'${entity}_id/'+#id")
	public ${entity} load(Object id) {
		QueryResult result = SpringJDBCBuilder.getInstance().buildDAL().selectByPrimaryKey(${entity}.class, id);
		if (result != null) {
			return result.as(${entity}.class);
		}
		return null;
	}
	@Caching(evict = { @CacheEvict(value = "${cacheName}", key = "'${entity}_id/'+#entity.${primaryKey}") })
	public void delete(${entity} entity) {
		SpringJDBCBuilder.getInstance().buildDAL().deleteByPrimaryKey(${entity}.class, entity.get${primaryKey?capitalize}());
	}
	@Caching(evict = { @CacheEvict(value = "${cacheName}", key = "'${entity}_id/'+#entity.${primaryKey}") })
	public void update(${entity} entity) {
		SpringJDBCBuilder.getInstance().buildDAL().updateByPrimaryKey(entity);
	}
	public long insert(${entity} entity) {
		return SpringJDBCBuilder.getInstance().buildDAL().insert(entity);
	}
	protected IDao${entity} currentProxy() {
		return (IDao${entity})AopContext.currentProxy();
	}

}
