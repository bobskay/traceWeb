package wang.wangby.entity;

import wang.wangby.annotation.Remark;

import java.util.ArrayList;
import java.util.List;

@Remark("分页的查询结果")
public class Pagination<T> {
	public static final int DEFAULT_SIZE = 5;

	@Remark("记录起始位置")
	private long offset = 0;
	@Remark("每页显示条数")
	private int pageSize = DEFAULT_SIZE;
	@Remark("查询结果")
	private List result;
	@Remark("总记录条数")
	private long totalCount;// 总条数

	public Pagination() {
		this(0,new ArrayList<>(),0,0);
	}


	public Pagination(long totalCount, List<T> result, long offset, int pageSize) {
		this.totalCount = totalCount;
		this.result = result;
		this.pageSize = pageSize;
		if (offset <= 0) {
			offset = 0;
		}
		if (offset > totalCount) {
			offset = totalCount;
		}
		this.offset = offset;
	}

	public Pagination(List<T> list) {
		this(list.size(), list, 1, list.size());
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List result) {
		this.result = result;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset){
		this.offset=offset;
	}

}
