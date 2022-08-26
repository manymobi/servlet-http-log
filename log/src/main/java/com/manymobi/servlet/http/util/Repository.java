package com.manymobi.servlet.http.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 梁建军
 * 创建日期： 2020/5/6
 * 创建时间： 下午4:29
 * @version 1.0
 * @since 1.0
 * String匹配查询库
 */
public class Repository<T> {


    protected final Entity<T> root;
    /**
     * 拆分字符串
     */
    protected final String splitRegex;

    /**
     * 匹配单个
     */
    protected final String matchSingle;

    /**
     * 匹配多个
     */
    protected final String matchMultiple;


    protected Repository(AbstractBuilder<T, ? extends Repository<T>> builder) {
        splitRegex = builder.splitRegex;
        matchSingle = builder.matchSingle;
        matchMultiple = builder.matchMultiple;


        Entity<T> rootTemp = new Entity<>("root");

        //把添加进入的地址转成树
        builder.mapMap.forEach((url, t) -> {

            Entity<T> temp = rootTemp;

            String[] strings = splitString(url);
            for (int i = 0; i < strings.length; i++) {
                ArrayKey arrayKey = new ArrayKey(strings[i]);
                if (Objects.equals(strings[i], matchSingle)) {
                    if (temp.matchSingleEntity == null) {
                        temp.matchSingleEntity = new Entity<T>(new ArrayKey(matchSingle));
                    }
                    temp = temp.matchSingleEntity;
                } else if (Objects.equals(strings[i], matchMultiple)) {
                    if (temp.matchMultipleEntity == null) {
                        temp.matchMultipleEntity = new Entity<T>(new ArrayKey(matchMultiple))
                                .data(t);
                        return;
                    }
                } else {
                    temp = temp.node.computeIfAbsent(arrayKey, Entity::new);
                }

                if (i == strings.length - 1) {
                    temp.data(t);
                }
            }
        });

        init(rootTemp);

        root = rootTemp;
    }

    /**
     * 降低树的深度,优化查询效率
     */
    private void init(Entity<T> rootTemp) {

        //用来记录是否还需要继续优化当前层
        boolean optimization = rootTemp.node.size() > 0;
        while (optimization) {

            // 合并的树高度
            int height = -1;
            for (Map.Entry<ArrayKey, Entity<T>> entry : rootTemp.node.entrySet()) {
                Entity<T> value = entry.getValue();
                if (value.matchSingleEntity != null || value.matchMultipleEntity != null || value.data != null) {
                    optimization = false;
                    break;
                }
                if (height != value.getSubsection()) {
                    if (height == -1) {
                        height = value.getSubsection();
                    } else {
                        optimization = false;
                        break;
                    }
                }

            }
            if (optimization) {
                List<Entity<T>> list1 = new ArrayList<>();
                for (Map.Entry<ArrayKey, Entity<T>> entry : rootTemp.node.entrySet()) {
                    for (Map.Entry<ArrayKey, Entity<T>> arrayKeyEntityEntry : entry.getValue().node.entrySet()) {
                        Entity<T> value = arrayKeyEntityEntry.getValue();
                        ArrayKey arrayKey = new ArrayKey(entry.getKey(), value.getKey());
                        Entity<T> objectEntity = new Entity<>(arrayKey);
                        objectEntity.matchSingleEntity = value.matchSingleEntity;
                        objectEntity.matchMultipleEntity = value.matchMultipleEntity;
                        objectEntity.data = value.data;
                        objectEntity.subsection = value.subsection;
                        objectEntity.node.putAll(value.node);
                        list1.add(objectEntity);
                    }
                }
                rootTemp.node.clear();
                for (Entity<T> tEntity : list1) {
                    rootTemp.node.put(tEntity.getKey(), tEntity);
                }
                rootTemp.subsection += height;
            }
        }
        rootTemp.node.forEach((arrayKey, entity) -> {
            init(entity);
        });

    }

    /**
     * 把url 进行按照  / 拆分成数组
     * 去掉重复 /
     * 例如 ： /api/order/{orderId}/show   ->  ["api","order","*","show"]
     *
     * @param s 切割内容
     * @return 切割完后的内容
     */
    private String[] splitString(String s) {
        String[] split = s.split(splitRegex);
        List<String> list = new ArrayList<>(split.length);
        for (String temp : split) {
            if (skip(temp)) {
                continue;
            }
            list.add(writeContent(temp));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 跳过
     *
     * @param s 内容
     * @return 跳过
     */
    protected boolean skip(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * 写入内容
     *
     * @param content 内容
     * @return 处理之后的
     */
    protected String writeContent(String content) {
        return content;
    }

    /**
     * 查询匹配的地址
     *
     * @param url 请求的url 地址
     * @return 数据对象
     */
    public Optional<T> find(String url) {
        String[] strings = splitString(url);
        return find(root, strings, 0, root.subsection);
    }

    private Optional<T> find(Entity<T> root, String[] url, int fromIndex, int subsection) {
        int toIndex = Math.min(url.length - fromIndex, subsection) + fromIndex;
        Entity<T> tEntity = root.node.get(new ArrayKeyTemp(url, fromIndex, toIndex));
        if (tEntity != null) {
            if (toIndex == url.length) {
                if (tEntity.data != null) {
                    return Optional.of(tEntity.data);
                }
            } else {
                Optional<T> t = find(tEntity, url, toIndex, tEntity.subsection);
                if (t.isPresent()) {
                    return t;
                }
            }
        }
        if (root.matchSingleEntity != null) {
            if (fromIndex + 1 == url.length) {
                if (root.matchSingleEntity.data != null) {
                    return Optional.of(root.matchSingleEntity.data);
                }
            } else {
                Optional<T> t = find(root.matchSingleEntity, url, fromIndex + 1, root.matchSingleEntity.subsection);
                if (t.isPresent()) {
                    return t;
                }
            }
        }

        if (root.matchMultipleEntity != null) {
            return Optional.ofNullable(root.matchMultipleEntity.data);
        }
        return Optional.empty();
    }

    private static class ArrayKeyTemp extends ArrayKey {
        private final String[] strings;

        private final int fromIndex;
        private final int toIndex;

        public ArrayKeyTemp(String[] strings1, int fromIndex, int toIndex) {
            super(strings1);
            this.strings = strings1;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }

        @Override
        public boolean equals(Object o) {
            ArrayKey arrayKey = (ArrayKey) o;

            int length = arrayKey.strings.length;
            if (toIndex - fromIndex != length)
                return false;

            for (int i = 0; i < length; i++) {
                Object o1 = arrayKey.strings[i];
                Object o2 = strings[i + fromIndex];
                if (!o1.equals(o2))
                    return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int temp = 0;
            for (int i = fromIndex; i < toIndex; i++) {
                temp ^= strings[i].hashCode();

            }
            return temp;
        }
    }

    private static class ArrayKey {

        private final String[] strings;

        public ArrayKey(String[] strings, int fromIndex, int toIndex) {
            int size = toIndex - fromIndex;
            this.strings = new String[size];
            System.arraycopy(strings, fromIndex, this.strings, 0, size);
        }


        public ArrayKey(String... strings) {
            this.strings = strings;
        }

        public ArrayKey(ArrayKey arrayKey, ArrayKey arrayKey1) {
            this.strings = new String[arrayKey.strings.length + arrayKey1.strings.length];
            System.arraycopy(arrayKey.strings, 0, this.strings, 0, arrayKey.strings.length);
            System.arraycopy(arrayKey1.strings, 0, this.strings, arrayKey.strings.length, arrayKey1.strings.length);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            ArrayKey arrayKey = (ArrayKey) o;
            return Arrays.equals(strings, arrayKey.strings);
        }

        @Override
        public int hashCode() {
            int temp = 0;
            for (String string : strings) {
                temp ^= string.hashCode();
            }
            return temp;
        }

        @Override
        public String toString() {
            return Arrays.toString(strings);
        }

        public ArrayKey sub(int fromIndex) {
            return new ArrayKey(strings, fromIndex, strings.length);
        }

        public ArrayKey sub(int fromIndex, int toIndex) {
            return new ArrayKey(strings, fromIndex, toIndex);
        }

        public ArrayKey[] split(int fromIndex) {
            ArrayKey[] arrayKeys = new ArrayKey[2];
            arrayKeys[0] = sub(0, Math.min(fromIndex, strings.length));
            if (strings.length > fromIndex) {
                arrayKeys[1] = sub(fromIndex);
            }
            return arrayKeys;
        }
    }

    @Override
    public String toString() {
        return "UrlRepository{" +
                "root=" + root +
                '}';
    }

    /**
     * 内部实体
     *
     * @param <T> 自定义数据
     */
    private static class Entity<T> {
        /**
         * key
         */
        private ArrayKey key;
        /**
         * 匹配单个
         */
        private Entity<T> matchSingleEntity;

        /**
         * 匹配多个
         */
        private Entity<T> matchMultipleEntity;
        /**
         * 下一级数据
         */

        private final Map<ArrayKey, Entity<T>> node;
        /**
         * 数据
         */
        private T data;

        private int subsection;

        public Entity(ArrayKey key) {
            this.key = key;
            this.node = new HashMap<>();
            this.data = null;
            this.matchSingleEntity = null;
            this.matchMultipleEntity = null;
            this.subsection = 1;
        }

        public Entity(String... keys) {
            this.key = new ArrayKey(keys);
            this.node = new HashMap<>();
            this.data = null;
            this.matchSingleEntity = null;
            this.matchMultipleEntity = null;
            this.subsection = 1;
        }

        public Entity<T> data(T data) {
            this.data = data;
            return this;
        }


        public ArrayKey getKey() {
            return key;
        }

        public int getSubsection() {
            return subsection;
        }


        @Override
        public String toString() {
            return "Entity{" +
                    "key=" + key +
                    ", matchSingle=" + matchSingleEntity +
                    ", matchMultiple=" + matchMultipleEntity +
                    ", data=" + data +
                    ", subsection=" + subsection +
                    ", root=" + node +
                    '}';
        }
    }


    public static class Builder<T> extends AbstractBuilder<T, Repository<T>> {
        @Override
        public Repository<T> build() {
            return new Repository<>(this);
        }
    }

    public abstract static class AbstractBuilder<T, R extends Repository<T>> {

        private final Map<String, T> mapMap = new HashMap<>();

        /**
         * 拆分字符串
         */
        protected String splitRegex;

        /**
         * 匹配单个
         */
        protected String matchSingle;

        /**
         * 匹配多个
         */
        protected String matchMultiple;

        public AbstractBuilder() {
            splitRegex = "/";
            matchSingle = "*";
            matchMultiple = "**";
        }

        /**
         * 添加请求方法
         *
         * @param url url 地址
         *            例如 ：
         *            /api/order/{orderId}
         *            /api/order/page
         *            /api/order/**
         *            /api/order/*
         * @param t   数据
         * @return this
         */
        protected AbstractBuilder<T, R> add(String url, T t) {
            mapMap.put(url, t);
            return this;
        }

        protected AbstractBuilder<T, R> setSplitRegex(String splitRegex) {
            this.splitRegex = splitRegex;
            return this;
        }

        protected AbstractBuilder<T, R> setMatchSingle(String matchSingle) {
            this.matchSingle = matchSingle;
            return this;
        }

        protected AbstractBuilder<T, R> setMatchMultiple(String matchMultiple) {
            this.matchMultiple = matchMultiple;
            return this;
        }

        public abstract R build();
    }
}
