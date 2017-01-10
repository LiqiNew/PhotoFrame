package com.liql.photograph.interfa;

/**
 * 路径转换其它对象接口
 * 
 * @author Liqi
 * 
 * @param <T>
 */
public interface OnPhotographDispose<T> {
	/**
	 * 把路径转换成其它对象
	 * 
	 * @param path
	 * @return
	 */
	public T getPhotographDisposeData(String path);
}
