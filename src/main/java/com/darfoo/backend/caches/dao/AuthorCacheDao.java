package com.darfoo.backend.caches.dao;

import com.darfoo.backend.caches.client.AbstractBaseRedisDao;
import com.darfoo.backend.caches.cota.CacheProtocol;
import com.darfoo.backend.model.resource.Author;
import com.darfoo.backend.service.responsemodel.SingleAuthor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zjh on 15-1-3.
 */
public class AuthorCacheDao extends AbstractBaseRedisDao<String, Author> {
    @Autowired
    CacheProtocol cacheProtocol;

    /**
     * 为单个作者进行缓存
     *
     * @param author
     * @return
     */
    public boolean insertSingleAuthor(Author author) {
        return cacheProtocol.insertResourceIntoCache(Author.class, author, "author");
    }

    public SingleAuthor getSingleAuthor(Integer id) {
        return (SingleAuthor) cacheProtocol.extractResourceFromCache(SingleAuthor.class, id, "author");
    }
}
