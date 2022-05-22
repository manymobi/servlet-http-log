package com.manymobi.servlet.http.util;

import com.manymobi.servlet.http.HttpMethod;

import java.util.Optional;

/**
 * @author 梁建军
 * 创建日期： 2022/5/9
 * 创建时间： 下午8:24
 * @version 1.0
 * @since 1.0
 */
public class URLPathRepository<T> extends Repository<T> {

    protected URLPathRepository(AbstractBuilder<T, ? extends Repository<T>> builder) {
        super(builder);
    }

    @Override
    protected String writeContent(String content) {
        // 将{id}这个形式的转换成匹配单个
        if (content.charAt(0) == '{' && content.charAt(content.length() - 1) == '}') {
            return matchSingle;
        }
        return super.writeContent(content);
    }

    public Optional<T> find(HttpMethod httpMethod, String url) {
        return super.find(httpMethod + splitRegex + url);
    }

    public Optional<T> find(String httpMethod, String url) {
        return super.find(httpMethod + splitRegex + url);
    }

    public static class Builder<T> extends AbstractBuilder<T, Repository<T>> {

        public Builder() {
            setMatchMultiple("**");
            setMatchSingle("*");
            setSplitRegex("/");
        }

        public Builder<T> addPath(HttpMethod httpMethod, String url, T t) {
            super.add(httpMethod + splitRegex + url, t);
            return this;
        }

        @Override
        public URLPathRepository<T> build() {
            return new URLPathRepository<>(this);
        }
    }

}
