package com.wangzl.apprunconfig;
import java.util.ArrayList;
/**
 * ע��������Ŵ�1��ʼ.
 * 
 * @param <T> Page�м�¼������.
 * 
 */
public class Page<T> {

	//-- ��ҳ���� --//
	protected int pageNo = 0;// ��ǰҳ��<��ȡ���ݵķ�ʽ�й�ϵ>
	protected int pageSize = 1;// ÿҳ��ʾ�ļ�¼��
	protected String orderBy = null;
	protected String order = null;
	protected boolean autoCount = true;
	//-- ���ؽ�� --//
	protected ArrayList<T> result = null;
	protected long totalCount = -1;// �ܼ�¼��
	//-- ���캯�� --//
	public Page() {
	}
	public Page(final int pageSize) {
		setPageSize(pageSize);
	}
	public Page(final int pageSize, final boolean autoCount) {
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}
	//-- ���ʲ�ѯ�������� --//
	/**
	 * ��õ�ǰҳ��ҳ��,��Ŵ�0��ʼ,Ĭ��Ϊ0.
	 */
	public int getPageNo() {
		return pageNo;
	}
	/**
	 * ���õ�ǰҳ��ҳ��,��Ŵ�0��ʼ,����0ʱ�Զ�����Ϊ0.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;
		if (pageNo < 0) {
			this.pageNo = 0;
		}
	}
	/**
	 * ���ÿҳ�ļ�¼����,Ĭ��Ϊ1.
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * ����ÿҳ�ļ�¼����,����0ʱ�Զ�����Ϊ0.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
		if (pageSize < 0) {
			this.pageSize = 0;
		}
	}
	/**
	 * ����pageNo��pageSize���㵱ǰҳ��һ����¼���ܽ�����е�λ��,��Ŵ�0��ʼ.
	 */
	public int getFirst() {
		return (pageNo * pageSize) + 1;
	}
	/**
	 * ��������ֶ�,��Ĭ��ֵ.��������ֶ�ʱ��','�ָ�.
	 */
	public String getOrderBy() {
		return orderBy;
	}
	/**
	 * ���������ֶ�,��������ֶ�ʱ��','�ָ�.
	 */
	public void setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * ���������.
	 */
	public String getOrder() {
		return order;
	}
	/**
	 * ��������ʽ.
	 * 
	 * @param order ��ѡֵΪdesc��asc
	 */
	public void setOrder(String order) {
		this.order = order;
	}
	/**
	 * ��ѯ����ʱ�Ƿ��Զ�����ִ��count��ѯ��ȡ�ܼ�¼��, Ĭ��Ϊfalse.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}
	/**
	 * ��ѯ����ʱ�Ƿ��Զ�����ִ��count��ѯ��ȡ�ܼ�¼��.
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}
	//-- ���ʲ�ѯ������� --//
	/**
	 * ȡ��ҳ�ڵļ�¼�б�.
	 */
	public ArrayList<T> getResult() {
		return result;
	}
	/**
	 * ����ҳ�ڵļ�¼�б�.
	 */
	public void setResult(final ArrayList<T> result) {
		this.result = result;
	}
	/**
	 * ȡ���ܼ�¼��, Ĭ��ֵΪ-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}
	/**
	 * �����ܼ�¼��.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * ����pageSize��totalCount������ҳ��, Ĭ��ֵΪ-1.
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
	 * �Ƿ�����һҳ.
	 */
	public boolean isHasNext() {
		return (pageNo + 1 < getTotalPages());
	}
	/**
	 * ȡ����ҳ��ҳ��, ��Ŵ�0��ʼ.
	 * ��ǰҳΪβҳʱ�Է���βҳ���.
	 */
	public int getNextPage() {
		if (isHasNext())
			return pageNo + 1;
		else
			return pageNo;
	}
	/**
	 * �Ƿ�����һҳ.
	 */
	public boolean isHasPre() {
		return (pageNo - 1 >= 0);
	}
	/**
	 * ȡ����ҳ��ҳ��, ��Ŵ�1��ʼ.
	 * ��ǰҳΪ��ҳʱ������ҳ���.
	 */
	public int getPrePage() {
		if (isHasPre())
			return pageNo - 1;
		else
			return pageNo;
	}
}