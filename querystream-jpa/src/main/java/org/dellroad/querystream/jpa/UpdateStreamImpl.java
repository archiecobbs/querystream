
/*
 * Copyright (C) 2018 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.querystream.jpa;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.dellroad.querystream.jpa.querytype.UpdateType;

class UpdateStreamImpl<X>
  extends QueryStreamImpl<X, Root<X>, CriteriaUpdate<X>, CriteriaUpdate<X>, Query, UpdateType<X>>
  implements UpdateStream<X> {

// Constructors

    UpdateStreamImpl(EntityManager entityManager, Class<X> type) {
        this(entityManager, new UpdateType<X>(type));
    }

    private UpdateStreamImpl(EntityManager entityManager, UpdateType<X> queryType) {
        super(entityManager, queryType, (builder, query) -> query.from(queryType.getType()), -1, -1);
    }

    private UpdateStreamImpl(EntityManager entityManager, UpdateType<X> queryType,
      QueryConfigurer<CriteriaUpdate<X>, X, ? extends Root<X>> configurer, int firstResult, int maxResults) {
        super(entityManager, queryType, configurer, firstResult, maxResults);
    }

// UpdateStream

    @Override
    public int update() {
        return this.toQuery().executeUpdate();
    }

    @Override
    public <Y> UpdateStream<X> set(Path<Y> attribute, Expression<? extends Y> value) {
        QueryStreamImpl.checkOffsetLimit(this, "set()");
        return this.modQuery((builder, query) -> query.set(attribute, value));
    }

    @Override
    public <Y, V extends Y> UpdateStream<X> set(Path<Y> attribute, V value) {
        QueryStreamImpl.checkOffsetLimit(this, "set()");
        return this.modQuery((builder, query) -> query.set(attribute, value));
    }

    @Override
    public <Y> UpdateStream<X> set(SingularAttribute<? super X, Y> attribute, Expression<? extends Y> value) {
        QueryStreamImpl.checkOffsetLimit(this, "set()");
        return this.modQuery((builder, query) -> query.set(attribute, value));
    }

    @Override
    public <Y, V extends Y> UpdateStream<X> set(SingularAttribute<? super X, Y> attribute, V value) {
        QueryStreamImpl.checkOffsetLimit(this, "set()");
        return this.modQuery((builder, query) -> query.set(attribute, value));
    }

// Subclass required methods

    @Override
    UpdateStream<X> create(EntityManager entityManager, UpdateType<X> queryType,
      QueryConfigurer<CriteriaUpdate<X>, X, ? extends Root<X>> configurer, int firstResult, int maxResults) {
        return new UpdateStreamImpl<>(entityManager, queryType, configurer, firstResult, maxResults);
    }

    @Override
    CriteriaUpdate<X> select(CriteriaUpdate<X> query, Root<X> selection) {
        return query;
    }

// Narrowing overrides (QueryStreamImpl)

    @Override
    public UpdateType<X> getQueryType() {
        return this.queryType;
    }

    @Override
    UpdateStream<X> modQuery(BiConsumer<? super CriteriaBuilder, ? super CriteriaUpdate<X>> modifier) {
        return (UpdateStream<X>)super.modQuery(modifier);
    }

    @Override
    public UpdateStream<X> bind(Ref<X, ? super Root<X>> ref) {
        return (UpdateStream<X>)super.bind(ref);
    }

    @Override
    public UpdateStream<X> peek(Consumer<? super Root<X>> peeker) {
        return (UpdateStream<X>)super.peek(peeker);
    }

    @Override
    public UpdateStream<X> filter(SingularAttribute<? super X, Boolean> attribute) {
        return (UpdateStream<X>)super.filter(attribute);
    }

    @Override
    public UpdateStream<X> filter(Function<? super Root<X>, ? extends Expression<Boolean>> predicateBuilder) {
        return (UpdateStream<X>)super.filter(predicateBuilder);
    }

    @Override
    public UpdateStream<X> limit(int limit) {
        return (UpdateStream<X>)super.limit(limit);
    }

    @Override
    public UpdateStream<X> skip(int skip) {
        return (UpdateStream<X>)super.skip(skip);
    }
}
