
/*
 * Copyright (C) 2018 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.querystream.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;

import org.dellroad.querystream.jpa.querytype.SearchType;

class SearchStreamImpl<X, S extends Selection<X>>
  extends QueryStreamImpl<X, S, AbstractQuery<?>, CriteriaQuery<X>, TypedQuery<X>, SearchType<X>>
  implements SearchStream<X, S> {

// Constructors

    SearchStreamImpl(EntityManager entityManager, SearchType<X> queryType,
      QueryConfigurer<AbstractQuery<?>, X, ? extends S> configurer, int firstResult, int maxResults) {
        super(entityManager, queryType, configurer, firstResult, maxResults);
    }

// Superclass overrides

    @Override
    SearchStream<X, S> create(EntityManager entityManager, SearchType<X> queryType,
      QueryConfigurer<AbstractQuery<?>, X, ? extends S> configurer, int firstResult, int maxResults) {
        return new SearchStreamImpl<>(entityManager, queryType, configurer, firstResult, maxResults);
    }

    @Override
    SearchStream<X, S> withConfig(QueryConfigurer<AbstractQuery<?>, X, ? extends S> configurer) {
        return (SearchStream<X, S>)super.withConfig(configurer);
    }

    @Override
    final CriteriaQuery<X> select(CriteriaQuery<X> query, S selection) {
        return query.select(selection);
    }

// Subclass required methods

    SearchValue<X, S> toValue() {
        return this.toValue(false);
    }

    SearchValue<X, S> toValue(boolean forceLimit) {
        return new SearchValueImpl<>(this.entityManager, this.queryType,
          this.configurer, this.firstResult, forceLimit ? 1 : this.maxResults);
    }

// CriteriaQuery stuff

    @Override
    public SearchStream<X, S> distinct() {
        QueryStreamImpl.checkOffsetLimit(this, "distinct() must be performed prior to skip() or limit()");
        return this.modQuery((builder, query) -> query.distinct(true));
    }

    @Override
    public SearchStream<X, S> orderBy(Ref<?, ? extends Expression<?>> ref, boolean asc) {
        if (ref == null)
            throw new IllegalArgumentException("null ref");
        return this.orderBy(selection -> ref.get(), asc);
    }

    @Override
    public SearchStream<X, S> orderBy(Order... orders) {
        if (orders == null)
            throw new IllegalArgumentException("null orders");
        return this.orderByMulti(selection -> Arrays.asList(orders));
    }

    @Override
    public SearchStream<X, S> orderBy(SingularAttribute<? super X, ?> attribute, boolean asc) {
        if (attribute == null)
            throw new IllegalArgumentException("null attribute");
        return this.orderBy(selection -> ((Path<X>)selection).get(attribute), asc);      // cast must be valid if attribute exists
    }

    @Override
    public SearchStream<X, S> orderBy(
      SingularAttribute<? super X, ?> attribute1, boolean asc1,
      SingularAttribute<? super X, ?> attribute2, boolean asc2) {
        if (attribute1 == null)
            throw new IllegalArgumentException("null attribute1");
        if (attribute2 == null)
            throw new IllegalArgumentException("null attribute2");
        return this.orderByMulti(selection -> {
            final Path<X> path = (Path<X>)selection;                                    // cast must be valid if attribute exists
            return Arrays.asList(
              asc1 ? this.builder().asc(path.get(attribute1)) : this.builder().desc(path.get(attribute1)),
              asc2 ? this.builder().asc(path.get(attribute2)) : this.builder().desc(path.get(attribute2)));
        });
    }

    @Override
    public SearchStream<X, S> orderBy(
      SingularAttribute<? super X, ?> attribute1, boolean asc1,
      SingularAttribute<? super X, ?> attribute2, boolean asc2,
      SingularAttribute<? super X, ?> attribute3, boolean asc3) {
        if (attribute1 == null)
            throw new IllegalArgumentException("null attribute1");
        if (attribute2 == null)
            throw new IllegalArgumentException("null attribute2");
        if (attribute3 == null)
            throw new IllegalArgumentException("null attribute3");
        return this.orderByMulti(selection -> {
            final Path<X> path = (Path<X>)selection;                                    // cast must be valid if attribute exists
            return Arrays.asList(
              asc1 ? this.builder().asc(path.get(attribute1)) : this.builder().desc(path.get(attribute1)),
              asc2 ? this.builder().asc(path.get(attribute2)) : this.builder().desc(path.get(attribute2)),
              asc3 ? this.builder().asc(path.get(attribute3)) : this.builder().desc(path.get(attribute3)));
        });
    }

    @Override
    public SearchStream<X, S> orderBy(Function<? super S, ? extends Expression<?>> orderExprFunction, boolean asc) {
        if (orderExprFunction == null)
            throw new IllegalArgumentException("null orderExprFunction");
        return this.orderByMulti(selection -> {
            final Expression<?> expr = orderExprFunction.apply(selection);
            return Collections.singletonList(asc ? this.builder().asc(expr) : this.builder().desc(expr));
        });
    }

    @Override
    public SearchStream<X, S> orderByMulti(Function<? super S, ? extends List<? extends Order>> orderListFunction) {
        if (orderListFunction == null)
            throw new IllegalArgumentException("null orderListFunction");
        QueryStreamImpl.checkOffsetLimit(this, "sorting must be performed prior to skip() or limit()");
        return this.withConfig((builder, query) -> {
            if (!(query instanceof CriteriaQuery)) {
                throw new UnsupportedOperationException("sorry, can't sort a subquery because"
                  + " the JPA Criteria API doesn't support sorting in subqueries");
            }
            final S selection = this.configure(builder, query);
            ((CriteriaQuery<?>)query).orderBy(new ArrayList<>(orderListFunction.apply(selection)));
            return selection;
        });
    }

    @Override
    public SearchStream<X, S> groupBy(Ref<?, ? extends Expression<?>> ref) {
        if (ref == null)
            throw new IllegalArgumentException("null ref");
        return this.groupBy(selection -> ref.get());
    }

    @Override
    public SearchStream<X, S> groupBy(SingularAttribute<? super X, ?> attribute) {
        if (attribute == null)
            throw new IllegalArgumentException("null attribute");
        return this.groupBy(selection -> ((Path<X>)selection).get(attribute));           // cast must be valid if attribute exists
    }

    @Override
    public SearchStream<X, S> groupBy(Function<? super S, ? extends Expression<?>> groupFunction) {
        if (groupFunction == null)
            throw new IllegalArgumentException("null groupFunction");
        return this.groupByMulti(selection -> Collections.singletonList(groupFunction.apply(selection)));
    }

    @Override
    public SearchStream<X, S> groupByMulti(Function<? super S, ? extends List<Expression<?>>> groupFunction) {
        if (groupFunction == null)
            throw new IllegalArgumentException("null groupFunction");
        QueryStreamImpl.checkOffsetLimit(this, "grouping must be performed prior to skip() or limit()");
        return this.withConfig((builder, query) -> {
            final S selection = this.configure(builder, query);
            query.groupBy(groupFunction.apply(selection));
            return selection;
        });
    }

    @Override
    public SearchStream<X, S> having(Function<? super S, ? extends Expression<Boolean>> havingFunction) {
        if (havingFunction == null)
            throw new IllegalArgumentException("null havingFunction");
        QueryStreamImpl.checkOffsetLimit(this, "grouping must be performed prior to skip() or limit()");
        return this.withConfig((builder, query) -> {
            final S selection = this.configure(builder, query);
            query.having(havingFunction.apply(selection));
            return selection;
        });
    }

// Streamy stuff

    @Override
    public SearchValue<X, S> findAny() {
        return this instanceof SearchValue ? (SearchValue<X, S>)this : this.toValue(true);
    }

    @Override
    public SearchValue<X, S> findFirst() {
        return this instanceof SearchValue ? (SearchValue<X, S>)this : this.toValue(true);
    }

// Binding

    @Override
    public <R> SearchStream<X, S> addRoot(Ref<R, ? super Root<R>> ref, Class<R> type) {
        if (ref == null)
            throw new IllegalArgumentException("null ref");
        if (type == null)
            throw new IllegalArgumentException("null type");
        QueryStreamImpl.checkOffsetLimit(this, "roots must be added prior to skip() or limit()");
        return this.withConfig((builder, query) -> {
            final S selection = this.configure(builder, query);
            ref.bind(query.from(type));
            return selection;
        });
    }

// Narrowing overrides (QueryStreamImpl)

    @Override
    public SearchType<X> getQueryType() {
        return this.queryType;
    }

    @Override
    SearchStream<X, S> modQuery(BiConsumer<? super CriteriaBuilder, ? super AbstractQuery<?>> modifier) {
        return (SearchStream<X, S>)super.modQuery(modifier);
    }

    @Override
    public SearchStream<X, S> bind(Ref<X, ? super S> ref) {
        return (SearchStream<X, S>)super.bind(ref);
    }

    @Override
    public SearchStream<X, S> peek(Consumer<? super S> peeker) {
        return (SearchStream<X, S>)super.peek(peeker);
    }

    @Override
    public <X2, S2 extends Selection<X2>> SearchStream<X, S> bind(
      Ref<X2, ? super S2> ref, Function<? super S, ? extends S2> refFunction) {
        return (SearchStream<X, S>)super.bind(ref, refFunction);
    }

    @Override
    public SearchStream<X, S> filter(SingularAttribute<? super X, Boolean> attribute) {
        return (SearchStream<X, S>)super.filter(attribute);
    }

    @Override
    public SearchStream<X, S> filter(Function<? super S, ? extends Expression<Boolean>> predicateBuilder) {
        return (SearchStream<X, S>)super.filter(predicateBuilder);
    }

    @Override
    public SearchStream<X, S> limit(int limit) {
        return (SearchStream<X, S>)super.limit(limit);
    }

    @Override
    public SearchStream<X, S> skip(int skip) {
        return (SearchStream<X, S>)super.skip(skip);
    }
}
