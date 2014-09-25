package com.wangzl.apprunconfig;
import java.util.ArrayList;
/**
 * 注意所有序号从1开始.
 * 
 * @param <T> Page中记录的类型.
 * 
 */
public class Page<T> {

	//-- 分页参数 --//
	protected int pageNo = 0;// 当前页号<跟取数据的方式有关系>
	protected int pageSize = 1;// 每页显示的记录数
	protected String orderBy = null;
	protected String order = null;
	protected boolean autoCount = true;
	//-- 返回结果 --//
	protected ArrayList<T> result = null;
	protected long totalCount = -1;// 总记录数
	//-- 构造函数 --//
	public Page() {
	}
	public Page(final int pageSize) {
		setPageSize(pageSize);
	}
	public Page(final int pageSize, final boolean autoCount) {
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}
	//-- 访问查询参数函数 --//
	/**
	 * 获得当前页的页号,序号从0开始,默认为0.
	 */
	public int getPageNo() {
		return pageNo;
	}
	/**
	 * 设置当前页的页号,序号从0开始,低于0时自动调整为0.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;
		if (pageNo < 0) {
			this.pageNo = 0;
		}
	}
	/**
	 * 获得每页的记录数量,默认为1.
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * 设置每页的记录数量,低于0时自动调整为0.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
		if (pageSize < 0) {
			this.pageSize = 0;
		}
	}
	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从0开始.
	 */
	public int getFirst() {
		return (pageNo * pageSize) + 1;
	}
	/**
	 * 获得排序字段,无默认值.多个排序字段时用','分隔.
	 */
	public String getOrderBy() {
		return orderBy;
	}
	/**
	 * 设置排序字段,多个排序字段时用','分隔.
	 */
	public void setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * 获得排序方向.
	 */
	public String getOrder() {
		return order;
	}
	/**
	 * 设置排序方式.
	 * 
	 * @param order 可选值为desc或asc
	 */
	public void setOrder(String order) {
		this.order = order;
	}
	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数, 默认为false.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}
	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数.
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}
	//-- 访问查询结果函数 --//
	/**
	 * 取得页内的记录列表.
	 */
	public ArrayList<T> getResult() {
		return result;
	}
	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final ArrayList<T> result) {
		this.result = result;
	}
	/**
	 * 取得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}
	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {
		if (totalCount < 0)
			return -1;
		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}
	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return (pageNo + 1 < getTotalPages());
	}
	/**
	 * 取得下页的页号, 序号从0开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNext())
			return pageNo + 1;
		else
			return pageNo;
	}
	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return (pageNo - 1 >= 0);
	}
	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPre())
			return pageNo - 1;
		else
			return pageNo;
	}
}