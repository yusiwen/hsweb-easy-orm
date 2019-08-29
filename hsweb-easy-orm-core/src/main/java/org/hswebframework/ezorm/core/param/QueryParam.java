package org.hswebframework.ezorm.core.param;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询参数
 *
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
public class QueryParam extends Param implements Serializable, Cloneable {
    private static final long serialVersionUID = 7941767360194797891L;

    public static final int DEFAULT_FIRST_PAGE_INDEX = Integer.getInteger("easyorm.page.fist.index", 0);

    public static final int DEFAULT_PAGE_SIZE = Integer.getInteger("easyorm.page.size", 25);

    /**
     * 是否进行分页，默认为true
     */
    private boolean paging = true;

    /**
     * 第一页索引
     *
     * @since 3.0.3
     */
    @Getter
    private int firstPageIndex = DEFAULT_FIRST_PAGE_INDEX;

    /**
     * 第几页
     */
    private int pageIndex = firstPageIndex;

    /**
     * 每页显示记录条数
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     *
     * @since 1.0
     */
    private List<Sort> sorts = new LinkedList<>();

    private transient int pageIndexTmp = 0;

    private boolean forUpdate = false;

    public Sort orderBy(String column) {
        Sort sort = new Sort(column);
        sorts.add(sort);
        return sort;
    }

    public <Q extends QueryParam> Q doPaging(int pageIndex) {
        this.pageIndex = pageIndex;
        this.paging = true;
        return (Q) this;
    }

    public <Q extends QueryParam> Q doPaging(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.paging = true;
        return (Q) this;
    }

    public <Q extends QueryParam> Q rePaging(int total) {
        paging = true;
        // 当前页没有数据后跳转到最后一页
        if (pageIndex != 0 && (pageIndex * pageSize) >= total) {
            int tmp = total / this.getPageSize();
            pageIndex = total % this.getPageSize() == 0 ? tmp - 1 : tmp;
        }
        return (Q) this;
    }


    public void setPageIndex(int pageIndex) {
        this.pageIndexTmp = this.pageIndex;
        this.pageIndex = Math.max(pageIndex - firstPageIndex, 0);
    }

    public void setFirstPageIndex(int firstPageIndex) {
        this.firstPageIndex = firstPageIndex;
        this.pageIndex = Math.max(this.pageIndexTmp - this.firstPageIndex, 0);
    }

    public int getThinkPageIndex() {
        return this.pageIndex + firstPageIndex;
    }

    @Override
    public QueryParam clone() {
        QueryParam sqlParam = ((QueryParam) super.clone());
        sqlParam.setExcludes(new LinkedHashSet<>(excludes));
        sqlParam.setIncludes(new LinkedHashSet<>(includes));
        sqlParam.setTerms(this.terms.stream().map(Term::clone).collect(Collectors.toList()));
        return sqlParam;
    }
}
