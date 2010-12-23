package org.rivu.image.tools.db;


import java.io.File;
import java.util.Date;

import org.rivu.image.core.RivuContext;


import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
 
public class GenDAO{
	private static EnvironmentConfig envConfig = new EnvironmentConfig();  
	private static Environment env = null ;
	private static StoreConfig storeConfig = new StoreConfig();    
	private static EntityStore store = null ;
	private static DataAccessor dataAccessor = null ;
	private static boolean sync = false ;
	static{
		envConfig.setAllowCreate(true);       
		envConfig.setTransactional(false);  
		envConfig.setCacheSize(5000000);       
		envConfig.setConfigParam("je.log.fileMax", String.valueOf(1000000000)); 
		 /* Open the entity store. */      
		env = new Environment(new File(RivuContext.SAVE_DB_DIR,"db"), envConfig);
		storeConfig.setAllowCreate(true);     
		storeConfig.setTransactional(false);
		storeConfig.setDeferredWrite(false);
		store = new EntityStore(env, "data", storeConfig);
		dataAccessor = new DataAccessor(store); 
	}
	/**
	 * �����ղض���
	 * @param favorites
	 */
	public static void saveRivuData(RivuData rivuData){
		synchronized (rivuData.id) {
			sync = true ;
			dataAccessor.id.putNoReturn(rivuData) ;
		}
	}
	/**
	 * �����ղض���
	 * @param favorites
	 */
	public static void saveRivuDataWrite(RivuData rivuData){
		synchronized (rivuData.id) {
			sync = true ;
			dataAccessor.id.put(rivuData) ;
		}
	}
	/**
	 * ��ȡ�ղض���
	 * @param id
	 * @return
	 */
	public static RivuData getDataByID(String id){
		return dataAccessor.id.get(id) ;
	}
	/**
	 * �����û�������û����е��ղؼ�¼
	 * @param user
	 * @return
	 */
	public static EntityCursor<RivuData> getDataByType(String type){
		return dataAccessor.type.subIndex(type).entities();
	}
	/**
	 * ɾ�������ղؼ�¼ 
	 */
	public static void clearByID(String id){
		synchronized (dataAccessor.id) {
			sync = true ;
			dataAccessor.id.delete(id) ;
		}
	}
	/**
	 * ɾ��ĳ���û��������ղؼ�¼
	 * @param user
	 */
	public static void clearByUser(String user){
		synchronized (dataAccessor.id) {
			sync = true ;
			dataAccessor.type.delete(user) ;
		}
	}
	/**
	 * ͬ�����ݵ��洢��
	 */
	public synchronized static void sync(){
		if(sync){
			sync = false ;
			store.sync() ;
			env.sync() ;
		}
	}
	/**
	 * �ر����ݿ����
	 */
	public static void close(){
		store.close(); 
		env.close();
	}
}
