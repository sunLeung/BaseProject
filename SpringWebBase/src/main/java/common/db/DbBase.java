package common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.ArrayUtils;

import com.mysql.jdbc.Statement;
import common.logger.Logger;
import common.logger.LoggerManger;
/**
 * 
 * @Description 数据库操作工具类
 * @author liangyx
 * @date 2013-6-29
 * @version V1.0
 */
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class DbBase {
	private static Logger log=LoggerManger.getLogger();

	protected QueryRunner runner = null;
	private final static ColumnListHandler columnListHandler = new ColumnListHandler();
	private final static MapListHandler mapListHandler = new MapListHandler();
	private final static ScalarHandler scaleHandler = new ScalarHandler();
	private static Map<Class, BeanHandler> beanHandlerMap = new HashMap<Class, BeanHandler>();
	private static Map<Class, BeanListHandler> beanListHandlerMap = new HashMap<Class, BeanListHandler>();
	private static Map<String, String> tableNameCache = new HashMap<String, String>();
	private static Map<String, String> insertSqlCache = new HashMap<String, String>();

	/*********************************** DB方法 ***********************************************/

	/**
	 * 查询数据库记录，返回以Class<T>为范型的一个List example:query(Test.class,"where id=?",1);
	 * 
	 * @param beanClass
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> List<T> query(Class<T> beanClass, String sql,
			Object... params) throws SQLException {
		boolean startSelect = sql.startsWith("select");
		if (!startSelect) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * ").append("from ")
					.append(getTableName(beanClass)).append(" ").append(sql);
			sql = sb.toString();
			sb = null;
		}
		Connection conn=getConnection();
		try {
			return (List<T>) runner.query(conn, sql,
					getBeanListHandler(beanClass), params);
		} finally {
			this.closeConnection(conn);
		}
	}

	/**
	 * 从数据库中读取id集的记录，不用缓存
	 * 
	 * @param beanClass
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> Map<Integer, T> readObjectByIds(
			Class<T> beanClass, List ids) throws SQLException {
		StringBuffer sb = new StringBuffer("select * from ");
		sb.append(getTableName(beanClass)).append(" where id in (");
		for (Object id : ids) {
			sb.append(id).append(",");
		}
		sb.deleteCharAt(sb.length() - 1).append(")");
		String sql = sb.toString();
		sb = null;
		BeanListHandler listHandler = getBeanListHandler(beanClass);
		List<T> rl = null;
		Connection conn=getConnection();
		try {
			rl = (List<T>) runner.query(conn, sql, listHandler);
		} finally {
			this.closeConnection(conn);
		}
		Map<Integer, T> m = new HashMap<Integer, T>();
		for (T t : rl) {
			m.put(t.getId(), t);
		}
		return m;
	}

	/**
	 * 得到一个范围内记录集
	 * 
	 * @param beanClass
	 * @param sql
	 * @param from
	 * @param count
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> List<T> queryRange(Class<T> beanClass, String sql,
			int from, int count, Object... params) throws SQLException {
		if (from < 0 || count < 0)
			throw new IllegalArgumentException(
					"Illegal parameter of 'from' or 'count', Must be positive.");
		count = from + count;
		return query(beanClass, sql + " LIMIT ?,?",
				ArrayUtils.addAll(params, new Object[] { from, count }));
	}

	/**
	 * 查找对象，一般复杂的SQL语句使用这个，返回一个以map为范型的List map的key为数据库相应的字段名,value为相应的数值
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> queryFields(String sql, Object... params)
			throws SQLException {
		Connection conn=getConnection();
		try {
			return (List<Map<String, Object>>) runner.query(conn,
					sql, mapListHandler, params);
		} finally {
			this.closeConnection(conn);
		}
	}

	/**
	 * 读取某个对象
	 * 
	 * @param beanClass
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> T read(Class<T> beanClass, String sql,
			Object... params) throws SQLException {

		if (!sql.startsWith("select ")) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * ");
			sb.append("from ").append(getTableName(beanClass)).append(" ")
					.append(sql);
			sql = sb.toString();
			sb = null;
		}
		Connection conn=getConnection();
		try {
			return (T) runner.query(conn, sql,
					getBeanHandler(beanClass), params);
		} finally {
			this.closeConnection(conn);
		}
	}

	/**
	 * 读取某个对象
	 * 
	 * @param beanClass
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> T read(Class<T> beanClass, long id)
			throws SQLException {
		String sql = "select * from " + getTableName(beanClass) + " where id=?";
		Connection conn=getConnection();
		try {
			return (T) runner.query(conn, sql,
					getBeanHandler(beanClass), id);
		} finally {
			sql = null;
			this.closeConnection(conn);
		}
	}

	/**
	 * 执行统计查询语句，语句的执行结果必须只返回一个数值
	 * 
	 * @param pojo
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> int stat(Class<T> pojo, String sql,
			Object... params) throws SQLException {
		Connection conn=getConnection();
		try {
			return ((Number) runner.query(conn, sql, scaleHandler,
					params)).intValue();
		} finally {
			this.closeConnection(conn);
		}
	}

	/**
	 * 
	 * 更新
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> int update(Class<T> pojo, String sql,
			Object... params) throws SQLException {
		StringBuffer sb = new StringBuffer();
		if (!sql.startsWith("update")) {
			sb.append("update ").append(getTableName(pojo)).append(" ")
					.append(sql);
		}
		Connection conn=getConnection();
		try {
			int rs = runner.update(conn, sb.toString(), params);
			return rs;
		} finally {
			sb = null;
			this.closeConnection(conn);
		}
	}

	/**
	 * 更新对象
	 * 
	 * @param obj
	 * @return
	 * @throws SQLException
	 */
	public int update(Pojo obj) throws SQLException {
		Map<String, String> pojo_bean = obj.listInsertableFields();
		String[] fields = pojo_bean.keySet().toArray(
				new String[pojo_bean.size()]);
		Object[] values = new Object[fields.length + 1];
		StringBuilder sql = new StringBuilder();
		sql.append("set ");
		for (int i = 0; i < fields.length; i++) {
			if (i > 0)
				sql.append(',');
			sql.append(fields[i]).append(" = ? ");
			values[i] = pojo_bean.get(fields[i]);
		}
		values[values.length - 1] = obj.getId();
		sql.append(" where id=?");
		int rs = update(obj.getClass(), sql.toString(), values);
		sql = null;
		return rs;
	}

	/**
	 * 更新
	 * 
	 * @param sql
	 * @param objs
	 * @return
	 * @throws SQLException
	 */
	public int[] updates(String sql, Object[][] objs) throws SQLException {
		Connection conn=getConnection();
		try {
			return runner.batch(conn, sql, objs);
		} finally {
			this.closeConnection(conn);
		}
	}

	/**
	 * 更新某个表的某个字段
	 * <p>
	 * id必需放在参数的最后一位
	 * 
	 * @param pojo
	 * @param col
	 * @param value
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> int update(Class<T> pojo, String col, Object value,
			int id) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("update ").append(getTableName(pojo)).append(" set ")
				.append(col).append(" = ? where id=?");
		Connection conn=getConnection();
		try {
			int rs = runner.update(conn, sb.toString(), value, id);
			return rs;
		} finally {
			sb = null;
			this.closeConnection(conn);
		}
	}

	/**
	 * 增加或减小一个Int字段值
	 * <p>
	 * id必需放在参数的最后一位
	 * 
	 * @param pojo
	 * @param col
	 * @param value
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> int updateDecOrAddIntValue(Class<T> pojo,
			String col, int value, int id) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("update ").append(getTableName(pojo)).append(" set ")
				.append(col).append(" = ").append(col)
				.append(" + ? where id=?");
		Connection conn=getConnection();
		try {
			int rs = runner.update(conn, sb.toString(), value, id);
			return rs;
		} finally {
			sb = null;
			this.closeConnection(conn);
		}
	}

	/**
	 * 执行删除语句
	 * 
	 * @param pojo
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <T extends Pojo> int delete(Class<T> pojo, int id)
			throws SQLException {
		StringBuffer sb = new StringBuffer("delete from ");
		sb.append(getTableName(pojo)).append(" where id=?");
		Connection conn=getConnection();
		try {
			int rs = runner.update(conn, sb.toString(), id);
			return rs;
		} finally {
			sb = null;
			this.closeConnection(conn);
		}
	}

	/**
	 * 插入对象
	 * <p>
	 * 注意：对象字段不能为数据库关键字
	 * @param obj
	 * @return
	 * @throws SQLException
	 */
	public int insert(Pojo obj) throws SQLException {
		Map<String, String> pojo_bean = obj.listInsertableFields();
		String[] fields = pojo_bean.keySet().toArray(
				new String[pojo_bean.size()]);
		String sql = getInsertSql(obj);
		PreparedStatement ps = null;
		ResultSet rs = null;
		int id = -1;
		Connection conn=getConnection();
		try {
			ps = conn.prepareStatement(sql.toString(),
					Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < fields.length; i++) {
				ps.setObject(i + 1, pojo_bean.get(fields[i]));
			}
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			id = rs.next() ? rs.getInt(1) : -1;
			obj.setId(id);
		} finally {
			fields = null;
			org.apache.commons.dbutils.DbUtils.closeQuietly(rs);
			org.apache.commons.dbutils.DbUtils.closeQuietly(ps);
			this.closeConnection(conn);
		}
		return id;
	}

	/**
	 * 批量插入对象
	 * <p>
	 * 注意：对象字段不能为数据库关键字
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int[] insert(List<Pojo> list) throws SQLException {
		if (list == null || list.size() == 0) {
			return null;
		}
		String sql = getInsertSql(list.get(0));
		PreparedStatement ps = null;
		String[] fields = null;
		int[] result = null;
		Connection conn=getConnection();
		try {
			ps = conn.prepareStatement(sql.toString());
			this.startTransaction(conn);
			for (Pojo obj : list) {
				Map<String, String> pojo_bean = obj.listInsertableFields();
				fields = pojo_bean.keySet().toArray(
						new String[pojo_bean.size()]);
				for (int i = 0; i < fields.length; i++) {
					ps.setObject(i + 1, pojo_bean.get(fields[i]));
				}
				ps.addBatch();
			}
			result = ps.executeBatch();
			this.commitTransaction(conn);
		} finally {
			fields = null;
			org.apache.commons.dbutils.DbUtils.closeQuietly(ps);
			this.closeConnection(conn);
		}
		return result;
	}
	
	public int[] update(List<Pojo> list) throws SQLException {
		if (list == null || list.size() == 0) {
			return null;
		}
		Map<String, String> pojo_bean = list.get(0).listInsertableFields();
		String[] fields = pojo_bean.keySet().toArray(
				new String[pojo_bean.size()]);
		StringBuilder sql = new StringBuilder();
		sql.append("set ");
		for (int i = 0; i < fields.length; i++) {
			if (i > 0)
				sql.append(',');
			sql.append(fields[i]).append(" = ? ");
		}
		sql.append(" where id=?");
		
		PreparedStatement ps = null;
		int[] result = null;
		Connection conn=getConnection();
		try {
			ps = conn.prepareStatement(sql.toString());
			this.startTransaction(conn);
			for (Pojo obj : list) {
				pojo_bean = obj.listInsertableFields();
				fields = pojo_bean.keySet().toArray(
						new String[pojo_bean.size()]);
				for (int i = 0; i < fields.length; i++) {
					ps.setObject(i + 1, pojo_bean.get(fields[i]));
				}
				ps.setObject(fields.length, obj.getId());
				ps.addBatch();
			}
			result = ps.executeBatch();
			this.commitTransaction(conn);
		} finally {
			fields = null;
			org.apache.commons.dbutils.DbUtils.closeQuietly(ps);
			this.closeConnection(conn);
		}
		return result;
	}

	/**
	 * 运行一条insert/update sql 没有缓存
	 * 
	 * @param sql
	 * @param obj
	 * @return
	 * @throws SQLException
	 */
	public int runInsertOrUpdateSQLNoCache(String sql, Object... obj)
			throws SQLException {
		Connection conn=getConnection();
		try {
			return runner.update(conn, sql, obj);
		} finally {
			this.closeConnection(conn);
		}
	}

	/*********************************** utils方法 ***********************************************/

	protected abstract void setQueryRunner();

	protected abstract Connection getConnection() throws SQLException;

	protected abstract void closeConnection(Connection conn);

	private void startTransaction(Connection conn) {
		try {
			if (conn != null) {
				conn.setAutoCommit(false);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void commitTransaction(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Pojo对应的数据库表名
	 * 
	 * @param c
	 * @return
	 */
	private static <T extends Pojo> String getTableName(Class<T> c) {
		try {
			String tn = tableNameCache.get(c.getSimpleName());
			if (tn == null) {
				tn = c.newInstance().tableName();
				tableNameCache.put(c.getSimpleName(), tn);
			}
			return tn;
		} catch (Exception e) {
			log.error("Get " + c.getSimpleName() + " name exception.");
			return null;
		}
	}

	/**
	 * 获取Pojo的插入sql语句
	 * 
	 * @param obj
	 * @return
	 */
	private static String getInsertSql(Pojo obj) {
		try {
			String insertSql = insertSqlCache.get(obj.getClass().getName());
			if (insertSql == null) {
				Map<String, String> pojo_bean = obj.listInsertableFields();
				String[] fields = pojo_bean.keySet().toArray(
						new String[pojo_bean.size()]);
				StringBuilder sql = new StringBuilder("INSERT INTO ");
				sql.append(obj.tableName());
				sql.append('(');
				for (int i = 0; i < fields.length; i++) {
					if (i > 0)
						sql.append(',');
					sql.append(fields[i]);
				}
				sql.append(") VALUES(");
				for (int i = 0; i < fields.length; i++) {
					if (i > 0)
						sql.append(',');
					sql.append('?');
				}
				sql.append(')');
				insertSql = sql.toString();
				sql = null;
				insertSqlCache.put(obj.getClass().getName(), insertSql);
			}
			return insertSql;
		} catch (Exception e) {
			log.error("Get " + obj.getClass().getSimpleName()
					+ " insertSql exception.");
			return null;
		}
	}

	private final static BeanListHandler getBeanListHandler(Class beanClass) {
		BeanListHandler listHandler = beanListHandlerMap.get(beanClass);
		if (listHandler == null) {
			listHandler = new BeanListHandler(beanClass);
			beanListHandlerMap.put(beanClass, listHandler);
		}
		return listHandler;
	}

	private final static BeanHandler getBeanHandler(Class beanClass) {
		BeanHandler bh = beanHandlerMap.get(beanClass);
		if (bh == null) {
			bh = new BeanHandler(beanClass);
			beanHandlerMap.put(beanClass, bh);
		}
		return bh;
	}

}
